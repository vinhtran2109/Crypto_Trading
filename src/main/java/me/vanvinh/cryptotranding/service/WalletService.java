package me.vanvinh.cryptotranding.service;

import lombok.RequiredArgsConstructor;
import me.vanvinh.cryptotranding.dto.WalletBalanceDTO;
import me.vanvinh.cryptotranding.entity.UserWallet;
import me.vanvinh.cryptotranding.repository.UserWalletRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserWalletRepository walletRepository;

    public List<WalletBalanceDTO> getUserWalletBalance(Long userId) {
        List<UserWallet> wallets = walletRepository.findByUserId(userId);
        return wallets.stream()
                .map(w -> new WalletBalanceDTO(w.getCurrency(), w.getBalance()))
                .collect(Collectors.toList());
    }
}