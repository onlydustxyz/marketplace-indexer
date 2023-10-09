package com.onlydust.marketplace.indexer.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ApplicationIT {
    public static void main(String[] args) {
        SpringApplication.run(MarketplaceIndexerApplication.class, args);
    }
}
