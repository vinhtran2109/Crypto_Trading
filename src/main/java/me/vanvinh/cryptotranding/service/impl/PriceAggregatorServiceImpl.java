package me.vanvinh.cryptotranding.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.vanvinh.cryptotranding.dto.PriceDataDto;
import me.vanvinh.cryptotranding.dto.PriceWithSource;
import me.vanvinh.cryptotranding.entity.AggregatedPrice;
import me.vanvinh.cryptotranding.provider.PriceProvider;
import me.vanvinh.cryptotranding.repository.AggregatedPriceRepository;
import me.vanvinh.cryptotranding.service.PriceAggregatorService;

@Service
@RequiredArgsConstructor
public class PriceAggregatorServiceImpl implements PriceAggregatorService {

    private static final Logger logger = LoggerFactory.getLogger(PriceAggregatorServiceImpl.class);

    private final AggregatedPriceRepository aggregatedPriceRepository;
    private final List<PriceProvider> priceProviders;
    private final Set<String> supportedSymbols;

    @Override
    @Scheduled(fixedDelay = 10000)  // chạy sau mỗi 10 giây kể từ khi task trước hoàn thành
    public void fetchAndStorePrices() {
        try {
            logger.info("Starting price aggregation from {} providers", priceProviders.size());
            
            // 1️⃣ Lấy giá từ tất cả providers
            List<PriceDataDto> allPrices = priceProviders.stream()
                    .map(PriceProvider::getLatestPrice)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (allPrices.isEmpty()) {
                logger.warn("No price data available from any provider");
                return;
            }

            // 2️⃣ Xử lý từng symbol
            for (String symbol : supportedSymbols) {
                // Lấy tất cả bid prices cho symbol này
                List<PriceWithSource> bidPrices = allPrices.stream()
                        .filter(dto -> dto.bids().containsKey(symbol))
                        .map(dto -> new PriceWithSource(dto.bids().get(symbol), dto.source()))
                        .collect(Collectors.toList());

                // Lấy tất cả ask prices cho symbol này
                List<PriceWithSource> askPrices = allPrices.stream()
                        .filter(dto -> dto.asks().containsKey(symbol))
                        .map(dto -> new PriceWithSource(dto.asks().get(symbol), dto.source()))
                        .collect(Collectors.toList());

                if (bidPrices.isEmpty() || askPrices.isEmpty()) {
                    logger.warn("Insufficient price data for symbol: {}", symbol);
                    continue;
                }

                // 3️⃣ Tìm best bid (highest) và best ask (lowest)
                PriceWithSource bestBid = bidPrices.stream()
                        .max(Comparator.comparing(PriceWithSource::price))
                        .orElse(null);

                PriceWithSource bestAsk = askPrices.stream()
                        .min(Comparator.comparing(PriceWithSource::price))
                        .orElse(null);

                if (bestBid == null || bestAsk == null) {
                    logger.warn("Failed to determine best prices for symbol: {}", symbol);
                    continue;
                }

                // 4️⃣ Lưu vào DB
                AggregatedPrice price = aggregatedPriceRepository.findBySymbol(symbol)
                        .orElseGet(AggregatedPrice::new);

                price.setSymbol(symbol);
                price.setBestBid(bestBid.price());
                price.setBestBidSource(bestBid.source());
                price.setBestAsk(bestAsk.price());
                price.setBestAskSource(bestAsk.source());
                price.setUpdatedAt(LocalDateTime.now());

                aggregatedPriceRepository.save(price);
                
                logger.info("Updated {} - Best Bid: {} ({}), Best Ask: {} ({})", 
                        symbol, bestBid.price(), bestBid.source(), 
                        bestAsk.price(), bestAsk.source());
            }

            logger.info("Price aggregation completed successfully");
        } catch (Exception e) {
            logger.error("Error during price aggregation: {}", e.getMessage(), e);
        }
    }
}
