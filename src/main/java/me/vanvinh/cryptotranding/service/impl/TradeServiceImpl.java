package me.vanvinh.cryptotranding.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.vanvinh.cryptotranding.component.LockingManager;
import me.vanvinh.cryptotranding.constant.Symbol;
import me.vanvinh.cryptotranding.constant.TradingSide;
import me.vanvinh.cryptotranding.dto.TradeRequestDTO;
import me.vanvinh.cryptotranding.dto.TradingPairDto;
import me.vanvinh.cryptotranding.entity.AggregatedPrice;
import me.vanvinh.cryptotranding.entity.Trade;
import me.vanvinh.cryptotranding.entity.UserWallet;
import me.vanvinh.cryptotranding.repository.AggregatedPriceRepository;
import me.vanvinh.cryptotranding.repository.TradeRepository;
import me.vanvinh.cryptotranding.repository.UserWalletRepository;
import me.vanvinh.cryptotranding.service.TradeService;

@Service
public class TradeServiceImpl implements TradeService {

    private static final Logger logger = LoggerFactory.getLogger(TradeServiceImpl.class);

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private AggregatedPriceRepository priceRepository;

    @Autowired
    private UserWalletRepository walletRepository;

    @Autowired
    private LockingManager lockingManager;

    @Autowired
    private Map<TradingSide, Map<Symbol, TradingPairDto>> tradingPairsConfig;

    @Override
    @Transactional
    public Trade executeTrade(Long userId, TradeRequestDTO dto) throws Exception {
        String lockKey = userId.toString();
        lockingManager.lock(lockKey);

        try {
            logger.info("Executing trade for userId={}, symbol={}, side={}, quantity={}",
                    userId, dto.getSymbol(), dto.getSide(), dto.getQuantity());

            // 1. Get current price
            AggregatedPrice price = priceRepository.findBySymbol(dto.getSymbol())
                    .orElseThrow(() -> {
                        logger.error("Symbol not found: {}", dto.getSymbol());
                        return new RuntimeException("Symbol does not exist: " + dto.getSymbol());
                    });

            // 2. Get trading pair from config using enums
            Symbol symbol = Symbol.valueOf(dto.getSymbol());
            TradingSide side = TradingSide.valueOf(dto.getSide());
            
            TradingPairDto tradingPair = tradingPairsConfig.get(side).get(symbol);
            if (tradingPair == null) {
                logger.error("Trading pair not configured for symbol={}, side={}", dto.getSymbol(), dto.getSide());
                throw new RuntimeException("Trading pair not configured");
            }
            
            String fromCurrency = tradingPair.fromCurrency().name();
            String toCurrency = tradingPair.toCurrency().name();
            
            logger.debug("Trading pair - From: {}, To: {}", fromCurrency, toCurrency);

            // 3. Get wallets (no pessimistic lock needed, using LockingManager instead)
            UserWallet fromWallet = walletRepository.findByUserIdAndCurrency(userId, fromCurrency)
                    .orElseThrow(() -> {
                        logger.error("Wallet not found for userId={}, currency={}", userId, fromCurrency);
                        return new RuntimeException("Wallet " + fromCurrency + " does not exist");
                    });

            UserWallet toWallet = walletRepository.findByUserIdAndCurrency(userId, toCurrency)
                    .orElseThrow(() -> {
                        logger.error("Wallet not found for userId={}, currency={}", userId, toCurrency);
                        return new RuntimeException("Wallet " + toCurrency + " does not exist");
                    });

            logger.debug("Retrieved wallets - From: {} balance={}, To: {} balance={}",
                    fromCurrency, fromWallet.getBalance(), toCurrency, toWallet.getBalance());

            // 4. Calculate trade price and total
            BigDecimal tradePrice = dto.getSide().equals("BUY") ? price.getBestAsk() : price.getBestBid();
            BigDecimal total = dto.getQuantity().multiply(tradePrice);

            logger.debug("Trade calculation - Price: {}, Total: {}", tradePrice, total);

            // 5. Execute trade - deduct from fromWallet, add to toWallet
            BigDecimal amountToDeduct = side == TradingSide.BUY ? total : dto.getQuantity();
            BigDecimal amountToAdd = side == TradingSide.BUY ? dto.getQuantity() : total;
            
            if (fromWallet.getBalance().compareTo(amountToDeduct) < 0) {
                logger.error("Insufficient balance for {} - userId={}, required={}, available={}",
                        side, userId, amountToDeduct, fromWallet.getBalance());
                throw new RuntimeException("Insufficient " + fromCurrency + " balance. Required: "
                        + amountToDeduct + ", Available: " + fromWallet.getBalance());
            }
            
            fromWallet.setBalance(fromWallet.getBalance().subtract(amountToDeduct));
            toWallet.setBalance(toWallet.getBalance().add(amountToAdd));

            logger.info("{} executed - userId={}, spent {} {}, received {} {}",
                    side, userId, amountToDeduct, fromCurrency, amountToAdd, toCurrency);

            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);

            // 6. Create trade record
            Trade trade = new Trade();
            trade.setUser(fromWallet.getUser());
            trade.setSymbol(symbol.name());
            trade.setQuantity(dto.getQuantity());
            trade.setSide(side.name());
            trade.setPrice(tradePrice);
            trade.setTimestamp(LocalDateTime.now());

            Trade savedTrade = tradeRepository.save(trade);
            logger.info("Trade saved successfully - tradeId={}, userId={}, symbol={}, side={}",
                    savedTrade.getId(), userId, dto.getSymbol(), dto.getSide());

            return savedTrade;

        } catch (Exception e) {
            logger.error("Trade execution failed - userId={}, symbol={}, error={}",
                    userId, dto.getSymbol(), e.getMessage(), e);
            throw new Exception(e);
        } finally {
            lockingManager.unlock(lockKey);
        }
    }

    @Override
    public List<Trade> getUserTradingHistory(Long userId) {
        logger.info("Retrieving trading history for userId={}", userId);
        List<Trade> trades = tradeRepository.findByUserId(userId);
        logger.info("Found {} trades for userId={}", trades.size(), userId);
        return trades;
    }
}