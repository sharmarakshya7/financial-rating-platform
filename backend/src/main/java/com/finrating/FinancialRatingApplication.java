package com.finrating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class FinancialRatingApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinancialRatingApplication.class, args);
    }
}
