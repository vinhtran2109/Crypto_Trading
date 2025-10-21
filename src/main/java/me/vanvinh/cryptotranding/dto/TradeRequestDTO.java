package me.vanvinh.cryptotranding.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeRequestDTO {
    private String symbol;
    private BigDecimal quantity;
    private String side; // "BUY" hoặc "SELL"
}
