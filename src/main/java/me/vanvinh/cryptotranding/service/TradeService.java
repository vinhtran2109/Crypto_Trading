package me.vanvinh.cryptotranding.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.vanvinh.cryptotranding.dto.TradeRequestDTO;
import me.vanvinh.cryptotranding.entity.AggregatedPrice;
import me.vanvinh.cryptotranding.entity.Trade;
import me.vanvinh.cryptotranding.entity.UserWallet;
import me.vanvinh.cryptotranding.repository.AggregatedPriceRepository;
import me.vanvinh.cryptotranding.repository.TradeRepository;
import me.vanvinh.cryptotranding.repository.UserWalletRepository;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final AggregatedPriceRepository priceRepository;
    private final UserWalletRepository walletRepository;

    @Transactional
    public Trade executeTrade(Long userId, TradeRequestDTO dto) {
        // 1. Lấy giá hiện tại
        AggregatedPrice price = priceRepository.findBySymbol(dto.getSymbol())
                .orElseThrow(() -> new RuntimeException("Symbol không tồn tại"));

        // 2. Tách symbol thành base và quote currency
        // Ví dụ: BTCUSDT -> base = BTC, quote = USDT
        String baseCurrency = extractBaseCurrency(dto.getSymbol());
        String quoteCurrency = extractQuoteCurrency(dto.getSymbol());

        // 3. Lấy ví base và quote
        UserWallet baseWallet = walletRepository.findByUser_IdAndCurrency(userId, baseCurrency)
                .orElseThrow(() -> new RuntimeException("Ví " + baseCurrency + " không tồn tại"));
        
        UserWallet quoteWallet = walletRepository.findByUser_IdAndCurrency(userId, quoteCurrency)
                .orElseThrow(() -> new RuntimeException("Ví " + quoteCurrency + " không tồn tại"));

        // 4. Tính tổng tiền giao dịch
        BigDecimal tradePrice = dto.getSide().equals("BUY") ? price.getBestAsk() : price.getBestBid();
        BigDecimal total = dto.getQuantity().multiply(tradePrice);

        // 5. Thực hiện giao dịch
        if(dto.getSide().equals("BUY")) {
            // Mua: trừ quote currency (USDT), cộng base currency (BTC)
            if(quoteWallet.getBalance().compareTo(total) < 0) {
                throw new RuntimeException("Số dư " + quoteCurrency + " không đủ để mua");
            }
            quoteWallet.setBalance(quoteWallet.getBalance().subtract(total));
            baseWallet.setBalance(baseWallet.getBalance().add(dto.getQuantity()));
        } else {
            // Bán: trừ base currency (BTC), cộng quote currency (USDT)
            if(baseWallet.getBalance().compareTo(dto.getQuantity()) < 0) {
                throw new RuntimeException("Số dư " + baseCurrency + " không đủ để bán");
            }
            baseWallet.setBalance(baseWallet.getBalance().subtract(dto.getQuantity()));
            quoteWallet.setBalance(quoteWallet.getBalance().add(total));
        }
        
        walletRepository.save(baseWallet);
        walletRepository.save(quoteWallet);

        // 6. Tạo giao dịch
        Trade trade = new Trade();
        trade.setUser(baseWallet.getUser());
        trade.setSymbol(dto.getSymbol());
        trade.setQuantity(dto.getQuantity());
        trade.setSide(dto.getSide());
        trade.setPrice(tradePrice);
        trade.setTimestamp(LocalDateTime.now());

        return tradeRepository.save(trade);
    }

    // Helper method để tách base currency từ symbol
    private String extractBaseCurrency(String symbol) {
        // BTCUSDT -> BTC, ETHUSDT -> ETH
        if (symbol.endsWith("USDT")) {
            return symbol.substring(0, symbol.length() - 4);
        }
        throw new RuntimeException("Symbol không hợp lệ");
    }

    // Helper method để tách quote currency từ symbol
    private String extractQuoteCurrency(String symbol) {
        // BTCUSDT -> USDT, ETHUSDT -> USDT
        if (symbol.endsWith("USDT")) {
            return "USDT";
        }
        throw new RuntimeException("Symbol không hợp lệ");
    }

    // Get user's trading history
    public List<Trade> getUserTradingHistory(Long userId) {
        return tradeRepository.findByUserId(userId);
    }
}

