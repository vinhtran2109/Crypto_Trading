package me.vanvinh.cryptotranding.controller;

import lombok.RequiredArgsConstructor;
import me.vanvinh.cryptotranding.dto.TradeRequestDTO;
import me.vanvinh.cryptotranding.entity.Trade;
import me.vanvinh.cryptotranding.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/{userId}")
    public ResponseEntity<Trade> trade(@PathVariable Long userId, @RequestBody TradeRequestDTO dto) {
        Trade trade = tradeService.executeTrade(userId, dto);
        return ResponseEntity.ok(trade);
    }
}
