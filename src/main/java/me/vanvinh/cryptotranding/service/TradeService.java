package me.vanvinh.cryptotranding.service;

import java.util.List;
import me.vanvinh.cryptotranding.dto.TradeRequestDTO;
import me.vanvinh.cryptotranding.entity.Trade;

public interface TradeService {
    Trade executeTrade(Long userId, TradeRequestDTO dto) throws Exception;
    List<Trade> getUserTradingHistory(Long userId);
}