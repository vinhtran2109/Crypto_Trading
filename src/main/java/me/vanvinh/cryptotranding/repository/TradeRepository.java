package me.vanvinh.cryptotranding.repository;

import me.vanvinh.cryptotranding.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByUserId(Long userId);
}