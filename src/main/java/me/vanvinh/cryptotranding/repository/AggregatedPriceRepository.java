package me.vanvinh.cryptotranding.repository;

import me.vanvinh.cryptotranding.entity.AggregatedPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AggregatedPriceRepository extends JpaRepository<AggregatedPrice, Long> {
    Optional<AggregatedPrice> findBySymbol(String symbol);
    Optional<AggregatedPrice> findFirstBySymbolOrderByUpdatedAtDesc(String symbol);

}