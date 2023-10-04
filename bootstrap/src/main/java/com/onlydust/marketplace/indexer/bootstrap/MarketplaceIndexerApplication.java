package com.onlydust.marketplace.indexer.bootstrap;

import com.onlydust.marketplace.indexer.postgres.adapter.PostgresConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableConfigurationProperties
@Import(PostgresConfiguration.class)
public class MarketplaceIndexerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceIndexerApplication.class, args);
    }

}
