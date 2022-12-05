package com.digitalbooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class DigitalbooksUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalbooksUserApplication.class, args);
	}

}
