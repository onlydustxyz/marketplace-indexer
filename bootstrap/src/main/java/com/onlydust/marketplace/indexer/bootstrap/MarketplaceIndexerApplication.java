package com.onlydust.marketplace.indexer.bootstrap;

import com.onlydust.marketplace.indexer.cron.JobScheduler;
import com.onlydust.marketplace.indexer.github.GithubConfiguration;
import com.onlydust.marketplace.indexer.postgres.PostgresConfiguration;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@EnableRetry
@Import({PostgresConfiguration.class, GithubConfiguration.class, JobScheduler.class})
public class MarketplaceIndexerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceIndexerApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Setting default JVM timezone as UTC
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
