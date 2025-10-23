package me.vanvinh.cryptotranding.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO for price data from a provider with source
 */
public record PriceDataDto(
        Map<String, BigDecimal> asks,
        Map<String, BigDecimal> bids,
        String source
) {
}
