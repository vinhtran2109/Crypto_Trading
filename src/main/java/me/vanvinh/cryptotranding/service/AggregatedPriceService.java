package me.vanvinh.cryptotranding.service;


import lombok.RequiredArgsConstructor;

import me.vanvinh.cryptotranding.entity.AggregatedPrice;
import me.vanvinh.cryptotranding.repository.AggregatedPriceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AggregatedPriceService {

    private final AggregatedPriceRepository aggregatedPriceRepository;

    public Optional<AggregatedPrice> getLatestPrice(String symbol) {
        return aggregatedPriceRepository.findFirstBySymbolOrderByUpdatedAtDesc(symbol);
    }
}
