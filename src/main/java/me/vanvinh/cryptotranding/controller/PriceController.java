package me.vanvinh.cryptotranding.controller;


import lombok.RequiredArgsConstructor;

import me.vanvinh.cryptotranding.entity.AggregatedPrice;
import me.vanvinh.cryptotranding.service.AggregatedPriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final AggregatedPriceService aggregatedPriceService;

    @GetMapping("/{symbol}")
    public ResponseEntity<?> getLatestPrice(@PathVariable String symbol) {
        Optional<AggregatedPrice> price = aggregatedPriceService.getLatestPrice(symbol.toUpperCase());
        return price
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
