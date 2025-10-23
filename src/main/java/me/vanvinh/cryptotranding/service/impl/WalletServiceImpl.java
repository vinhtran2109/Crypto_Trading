package me.vanvinh.cryptotranding.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.vanvinh.cryptotranding.dto.WalletBalanceDTO;
import me.vanvinh.cryptotranding.entity.UserWallet;
import me.vanvinh.cryptotranding.repository.UserWalletRepository;
import me.vanvinh.cryptotranding.service.WalletService;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserWalletRepository walletRepository;

    @Override
    public List<WalletBalanceDTO> getUserWalletBalance(Long userId) {
        List<UserWallet> wallets = walletRepository.findByUserId(userId);
        return wallets.stream()
                .map(w -> new WalletBalanceDTO(w.getCurrency(), w.getBalance()))
                .collect(Collectors.toList());
    }
}
