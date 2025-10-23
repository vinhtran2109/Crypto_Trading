package me.vanvinh.cryptotranding.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.vanvinh.cryptotranding.entity.AggregatedPrice;
import me.vanvinh.cryptotranding.repository.AggregatedPriceRepository;
import me.vanvinh.cryptotranding.service.AggregatedPriceService;

@Service
@RequiredArgsConstructor
public class AggregatedPriceServiceImpl implements AggregatedPriceService {

    private final AggregatedPriceRepository aggregatedPriceRepository;

    @Override
    public Optional<AggregatedPrice> getLatestPrice(String symbol) {
        return aggregatedPriceRepository.findFirstBySymbolOrderByUpdatedAtDesc(symbol);
    }
}
