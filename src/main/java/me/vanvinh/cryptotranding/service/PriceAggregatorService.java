package me.vanvinh.cryptotranding.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.vanvinh.cryptotranding.entity.AggregatedPrice;
import me.vanvinh.cryptotranding.repository.AggregatedPriceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceAggregatorService {

    private final AggregatedPriceRepository aggregatedPriceRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    private final List<String> supportedPairs = Arrays.asList("BTCUSDT", "ETHUSDT");

    @Scheduled(fixedRate = 10000)  // chạy mỗi 10 giây
    public void fetchAndStorePrices() {
        try {
            // 1️⃣ Lấy giá từ Binance
            String binanceUrl = "https://api.binance.com/api/v3/ticker/bookTicker";
            JsonNode binanceData = mapper.readTree(restTemplate.getForObject(binanceUrl, String.class));

            // 2️⃣ Lấy giá từ Huobi
            String huobiUrl = "https://api.huobi.pro/market/tickers";
            JsonNode huobiData = mapper.readTree(restTemplate.getForObject(huobiUrl, String.class)).get("data");


            for (String symbol : supportedPairs) {
                BigDecimal binanceBid = BigDecimal.ZERO;
                BigDecimal binanceAsk = BigDecimal.ZERO;
                BigDecimal huobiBid = BigDecimal.ZERO;
                BigDecimal huobiAsk = BigDecimal.ZERO;

                // ✅ Lấy giá Binance
                for (JsonNode node : binanceData) {
                    if (symbol.equals(node.get("symbol").asText())) {
                        binanceBid = new BigDecimal(node.get("bidPrice").asText());
                        binanceAsk = new BigDecimal(node.get("askPrice").asText());
                        break;
                    }
                }

                // ✅ Lấy giá Huobi
                for (JsonNode node : huobiData) {
                    if (symbol.equalsIgnoreCase(node.get("symbol").asText())) {
                        huobiBid = new BigDecimal(node.get("bid").asText());
                        huobiAsk = new BigDecimal(node.get("ask").asText());
                        break;
                    }
                }

                // ✅ So sánh và lấy giá tốt nhất
                BigDecimal bestBid = (binanceBid.compareTo(huobiBid) > 0) ? binanceBid : huobiBid;
                BigDecimal bestAsk = (binanceAsk.compareTo(huobiAsk) < 0) ? binanceAsk : huobiAsk;

                // ✅ Lưu vào DB
                AggregatedPrice price = aggregatedPriceRepository.findBySymbol(symbol)
                        .orElseGet(AggregatedPrice::new);

                price.setSymbol(symbol);
                price.setBestBid(bestBid);
                price.setBestAsk(bestAsk);
                price.setUpdatedAt(LocalDateTime.now());

                aggregatedPriceRepository.save(price);
            }


            System.out.println("✅ Updated aggregated prices successfully!");
        } catch (Exception e) {
            System.err.println("❌ Error updating prices: " + e.getMessage());
        }
    }
}