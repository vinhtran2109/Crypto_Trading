package me.vanvinh.cryptotranding.provider.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import me.vanvinh.cryptotranding.config.CryptoTradingConfig;
import static me.vanvinh.cryptotranding.constant.Constant.BINANCE_SOURCE;
import me.vanvinh.cryptotranding.dto.PriceDataDto;
import me.vanvinh.cryptotranding.dto.TradingPriceDto;
import me.vanvinh.cryptotranding.provider.PriceProvider;
import me.vanvinh.cryptotranding.util.FetchPriceUtils;
import reactor.core.publisher.Mono;

/**
 * Binance price provider implementation
 */
@Service
public class BinancePriceProvider implements PriceProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BinancePriceProvider.class);
    
    @Autowired
    private WebClient webClient;
    
    @Autowired
    private Set<String> supportedSymbols;
    
    @Override
    public PriceDataDto getLatestPrice() {
        Mono<String> binanceMono = webClient.get()
                .uri(CryptoTradingConfig.getBinanceUrl())
                .retrieve()
                .bodyToMono(String.class);

        String binResp = binanceMono.block();

        TradingPriceDto binancePrices;
        try {
            binancePrices = FetchPriceUtils.parseBinancePricesResponse(binResp, supportedSymbols, "askPrice",
                    "bidPrice");
        } catch (Exception e) {
            LOGGER.error("[BinanceProvider] err {}", e.getMessage(), e);
            return null;
        }
        Map<String, BigDecimal> binAsk = binancePrices.asks();
        Map<String, BigDecimal> binBid = binancePrices.bids();

        return new PriceDataDto(binAsk, binBid, BINANCE_SOURCE);
    }
    
    @Override
    public String getProviderName() {
        return BINANCE_SOURCE;
    }
}
