package me.vanvinh.cryptotranding.config;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import me.vanvinh.cryptotranding.constant.Constant;
import me.vanvinh.cryptotranding.constant.Currency;
import me.vanvinh.cryptotranding.constant.Symbol;
import me.vanvinh.cryptotranding.constant.TradingSide;
import me.vanvinh.cryptotranding.dto.TradingPairDto;
import me.vanvinh.cryptotranding.entity.UserWallet;
import me.vanvinh.cryptotranding.repository.UserWalletRepository;

@Configuration
public class CryptoTradingConfig {

    @Bean
    public CommandLineRunner setupWallet(UserWalletRepository userWalletRepository, 
                                         me.vanvinh.cryptotranding.repository.UserRepository userRepository) {
        return args -> {
            // Get or create user
            me.vanvinh.cryptotranding.entity.User user = userRepository.findById(Constant.USER_ID)
                    .orElseGet(() -> {
                        me.vanvinh.cryptotranding.entity.User newUser = new me.vanvinh.cryptotranding.entity.User();
                        newUser.setName("Default User");
                        return userRepository.save(newUser);
                    });

            UserWallet usdtWallet = new UserWallet();
            usdtWallet.setUser(user);
            usdtWallet.setBalance(new BigDecimal(50000));
            usdtWallet.setCurrency(Currency.USDT.name());
            userWalletRepository.save(usdtWallet);

            UserWallet btcWallet = new UserWallet();
            btcWallet.setUser(user);
            btcWallet.setBalance(BigDecimal.ZERO);
            btcWallet.setCurrency(Currency.BTC.name());
            userWalletRepository.save(btcWallet);

            UserWallet ethWallet = new UserWallet();
            ethWallet.setUser(user);
            ethWallet.setBalance(BigDecimal.ZERO);
            ethWallet.setCurrency(Currency.ETH.name());
            userWalletRepository.save(ethWallet);
        };
    }

    @Bean
    public WebClient webClient() {
        final int size = (int) DataSize.ofMegabytes(16).toBytes();
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder()
                .exchangeStrategies(strategies)
                .build();
    }

    @Bean
    public Set<String> supportedSymbols() {
        return Set.of(Symbol.ETHUSDT.name(), Symbol.BTCUSDT.name());
    }

    @Bean
    public Map<TradingSide, Map<Symbol, TradingPairDto>> tradingPairsConfig() {
        Map<TradingSide, Map<Symbol, TradingPairDto>> map = new HashMap<>();
        map.put(TradingSide.SELL, new HashMap<>() {{
            put(Symbol.BTCUSDT, new TradingPairDto(Currency.BTC, Currency.USDT));
            put(Symbol.ETHUSDT, new TradingPairDto(Currency.ETH, Currency.USDT));
        }});
        map.put(TradingSide.BUY, new HashMap<>() {{
            put(Symbol.BTCUSDT, new TradingPairDto(Currency.USDT, Currency.BTC));
            put(Symbol.ETHUSDT, new TradingPairDto(Currency.USDT, Currency.ETH));
        }});
        return map;
    }

    public static String getBinanceUrl() {
        return "https://api.binance.com/api/v3/ticker/bookTicker";
    }

    public static String getHuobiUrl() {
        return "https://api.huobi.pro/market/tickers";
    }
}
