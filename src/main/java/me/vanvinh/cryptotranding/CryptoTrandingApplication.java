package me.vanvinh.cryptotranding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoTrandingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoTrandingApplication.class, args);
    }

}
