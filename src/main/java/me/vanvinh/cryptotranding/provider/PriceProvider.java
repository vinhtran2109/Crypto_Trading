package me.vanvinh.cryptotranding.provider;

import me.vanvinh.cryptotranding.dto.PriceDataDto;

/**
 * Interface for price providers (exchanges)
 */
public interface PriceProvider {
    
    /**
     * Get latest prices from this provider
     * @return Price data with bid/ask for all supported symbols
     */
    PriceDataDto getLatestPrice();
    
    /**
     * Get provider name
     * @return Provider name (e.g., "BINANCE", "HUOBI")
     */
    String getProviderName();
}
