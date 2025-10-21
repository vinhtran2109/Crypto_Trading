package me.vanvinh.cryptotranding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WalletBalanceDTO {
    private String currency;
    private BigDecimal balance;
}