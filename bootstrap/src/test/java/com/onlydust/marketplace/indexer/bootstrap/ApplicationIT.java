package com.onlydust.marketplace.indexer.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationIT {
    public static void main(String[] args) {
        SpringApplication.run(MarketplaceIndexerApplication.class, args);
    }
}
