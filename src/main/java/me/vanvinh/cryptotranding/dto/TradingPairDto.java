package me.vanvinh.cryptotranding.dto;

import me.vanvinh.cryptotranding.constant.Currency;

/**
 * DTO representing a trading pair with from and to currencies
 */
public record TradingPairDto(
        Currency fromCurrency,
        Currency toCurrency
) {
}
