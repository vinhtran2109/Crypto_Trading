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
import static me.vanvinh.cryptotranding.constant.Constant.HUOBI_SOURCE;
import me.vanvinh.cryptotranding.dto.PriceDataDto;
import me.vanvinh.cryptotranding.dto.TradingPriceDto;
import me.vanvinh.cryptotranding.provider.PriceProvider;
import me.vanvinh.cryptotranding.util.FetchPriceUtils;
import reactor.core.publisher.Mono;

/**
 * Huobi price provider implementation
 */
@Service
public class HuobiPriceProvider implements PriceProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HuobiPriceProvider.class);
    
    @Autowired
    private WebClient webClient;
    
    @Autowired
    private Set<String> supportedSymbols;
    
    @Override
    public PriceDataDto getLatestPrice() {
        Mono<String> huobiMono = webClient.get()
                .uri(CryptoTradingConfig.getHuobiUrl())
                .retrieve()
                .bodyToMono(String.class);

        String huobiResp = huobiMono.block();

        TradingPriceDto huobiPrices;
        try {
            huobiPrices = FetchPriceUtils.parseHuobiPricesResponse(huobiResp, supportedSymbols, "ask",
                    "bid");
        } catch (Exception e) {
            LOGGER.error("[HuobiProvider] err {}", e.getMessage(), e);
            return null;
        }
        Map<String, BigDecimal> huobiAsk = huobiPrices.asks();
        Map<String, BigDecimal> huobiBid = huobiPrices.bids();

        return new PriceDataDto(huobiAsk, huobiBid, HUOBI_SOURCE);
    }
    
    @Override
    public String getProviderName() {
        return HUOBI_SOURCE;
    }
}
