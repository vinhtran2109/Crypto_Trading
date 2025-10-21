package me.vanvinh.cryptotranding.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.vanvinh.cryptotranding.dto.TradeRequestDTO;
import me.vanvinh.cryptotranding.entity.AggregatedPrice;
import me.vanvinh.cryptotranding.entity.Trade;
import me.vanvinh.cryptotranding.entity.UserWallet;
import me.vanvinh.cryptotranding.repository.AggregatedPriceRepository;
import me.vanvinh.cryptotranding.repository.TradeRepository;
import me.vanvinh.cryptotranding.repository.UserWalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

        // 2. Lấy ví người dùng
        UserWallet wallet = walletRepository.findByUserIdAndCurrency(userId, dto.getSymbol())
                .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

        BigDecimal total = dto.getQuantity().multiply(dto.getSide().equals("BUY") ? price.getBestAsk() : price.getBestBid());

        if(dto.getSide().equals("BUY") && wallet.getBalance().compareTo(total) < 0) {
            throw new RuntimeException("Số dư không đủ để mua");
        }

        if(dto.getSide().equals("BUY")) {
            wallet.setBalance(wallet.getBalance().subtract(total));
        } else {
            wallet.setBalance(wallet.getBalance().add(total));
        }
        walletRepository.save(wallet);

        // 3. Tạo giao dịch
        Trade trade = new Trade();
        trade.setUser(wallet.getUser());
        trade.setSymbol(dto.getSymbol());
        trade.setQuantity(dto.getQuantity());
        trade.setSide(dto.getSide());
        trade.setPrice(dto.getSide().equals("BUY") ? price.getBestAsk() : price.getBestBid());
        trade.setTimestamp(LocalDateTime.now());

        return tradeRepository.save(trade);
    }
}

