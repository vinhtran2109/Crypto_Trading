package me.vanvinh.cryptotranding.service;

import java.util.Optional;

import me.vanvinh.cryptotranding.entity.AggregatedPrice;

public interface AggregatedPriceService {
    Optional<AggregatedPrice> getLatestPrice(String symbol);
}
