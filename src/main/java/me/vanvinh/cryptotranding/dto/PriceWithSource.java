package me.vanvinh.cryptotranding.dto;

import java.math.BigDecimal;

/**
 * Record to hold price with its source
 */
public record PriceWithSource(BigDecimal price, String source) {
}
