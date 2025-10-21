package me.vanvinh.cryptotranding.repository;

import me.vanvinh.cryptotranding.entity.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {
    List<UserWallet> findByUserId(Long userId);
    Optional<UserWallet> findByUserIdAndCurrency(Long userId, String currency);
}
