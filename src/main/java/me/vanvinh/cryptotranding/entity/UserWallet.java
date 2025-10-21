package me.vanvinh.cryptotranding.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_wallets", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "currency"}))
public class UserWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // khóa ngoại

    private String currency; // USDT, BTC, ETH
    private BigDecimal balance;
}
