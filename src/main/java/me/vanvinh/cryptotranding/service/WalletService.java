package me.vanvinh.cryptotranding.service;

import java.util.List;

import me.vanvinh.cryptotranding.dto.WalletBalanceDTO;

public interface WalletService {
    List<WalletBalanceDTO> getUserWalletBalance(Long userId);
}