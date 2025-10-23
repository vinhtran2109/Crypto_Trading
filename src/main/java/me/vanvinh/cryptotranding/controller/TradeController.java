package me.vanvinh.cryptotranding.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.vanvinh.cryptotranding.dto.TradeRequestDTO;
import me.vanvinh.cryptotranding.entity.Trade;
import me.vanvinh.cryptotranding.service.TradeService;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/{userId}")
    public ResponseEntity<Trade> trade(@PathVariable Long userId, @RequestBody TradeRequestDTO dto) throws Exception {
        Trade trade = tradeService.executeTrade(userId, dto);
        return ResponseEntity.ok(trade);
    }

    // API: GET /api/trades/{userId} - Get user's trading history
    @GetMapping("/{userId}")
    public ResponseEntity<List<Trade>> getTradingHistory(@PathVariable Long userId) {
        List<Trade> trades = tradeService.getUserTradingHistory(userId);
        return ResponseEntity.ok(trades);
    }
}
