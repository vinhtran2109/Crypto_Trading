package me.vanvinh.cryptotranding.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Intermediate DTO for price data without source
 */
public record TradingPriceDto(
        Map<String, BigDecimal> asks,
        Map<String, BigDecimal> bids
) {
}
