package com.wallet.digitalwallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.wallet.digitalwallet.entity")
public class DigitalWalletApplication {
	public static void main(String[] args) {
		SpringApplication.run(DigitalWalletApplication.class, args);
	}
}
