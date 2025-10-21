package me.vanvinh.cryptotranding.controller;

import lombok.RequiredArgsConstructor;
import me.vanvinh.cryptotranding.dto.WalletBalanceDTO;
import me.vanvinh.cryptotranding.service.WalletService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // API: GET /api/wallets/{userId}
    @GetMapping("/api/wallets/{userId}")
    public List<WalletBalanceDTO> getWalletBalance(@PathVariable Long userId) {
        return walletService.getUserWalletBalance(userId);
    }
}