package me.vanvinh.cryptotranding.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "user_wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // trùng với cột user_id trong DB
    private User user;

    private String currency;

    private BigDecimal balance;
}
