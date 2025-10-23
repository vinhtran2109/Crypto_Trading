package me.vanvinh.cryptotranding.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.vanvinh.cryptotranding.dto.TradingPriceDto;

public class FetchPriceUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchPriceUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private FetchPriceUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Parse Binance prices response
     * @param response JSON response string
     * @param supportedSymbols Set of supported symbols
     * @param askFieldName Field name for ask price in JSON (e.g., "askPrice")
     * @param bidFieldName Field name for bid price in JSON (e.g., "bidPrice")
     * @return TradingPriceDto with asks and bids maps
     */
    public static TradingPriceDto parseBinancePricesResponse(
            String response, 
            Set<String> supportedSymbols, 
            String askFieldName,
            String bidFieldName) throws Exception {
        
        JsonNode data = objectMapper.readTree(response);
        Map<String, BigDecimal> asks = new HashMap<>();
        Map<String, BigDecimal> bids = new HashMap<>();
        
        for (JsonNode node : data) {
            String symbol = node.get("symbol").asText();
            if (supportedSymbols.contains(symbol)) {
                BigDecimal askPrice = new BigDecimal(node.get(askFieldName).asText());
                BigDecimal bidPrice = new BigDecimal(node.get(bidFieldName).asText());
                
                asks.put(symbol, askPrice);
                bids.put(symbol, bidPrice);
                
                LOGGER.debug("Parsed {} - Bid: {}, Ask: {}", symbol, bidPrice, askPrice);
            }
        }
        
        return new TradingPriceDto(asks, bids);
    }

    /**
     * Parse Huobi prices response
     * @param response JSON response string
     * @param supportedSymbols Set of supported symbols
     * @param askFieldName Field name for ask price in JSON (e.g., "ask")
     * @param bidFieldName Field name for bid price in JSON (e.g., "bid")
     * @return TradingPriceDto with asks and bids maps
     */
    public static TradingPriceDto parseHuobiPricesResponse(
            String response,
            Set<String> supportedSymbols,
            String askFieldName,
            String bidFieldName) throws Exception {
        
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode data = rootNode.get("data");
        
        Map<String, BigDecimal> asks = new HashMap<>();
        Map<String, BigDecimal> bids = new HashMap<>();
        
        for (JsonNode node : data) {
            String symbol = node.get("symbol").asText().toUpperCase();
            if (supportedSymbols.contains(symbol)) {
                BigDecimal askPrice = new BigDecimal(node.get(askFieldName).asText());
                BigDecimal bidPrice = new BigDecimal(node.get(bidFieldName).asText());
                
                asks.put(symbol, askPrice);
                bids.put(symbol, bidPrice);
                
                LOGGER.debug("Parsed {} - Bid: {}, Ask: {}", symbol, bidPrice, askPrice);
            }
        }
        
        return new TradingPriceDto(asks, bids);
    }
}
