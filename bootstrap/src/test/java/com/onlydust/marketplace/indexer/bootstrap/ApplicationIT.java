package com.onlydust.marketplace.indexer.bootstrap;

import com.onlydust.marketplace.indexer.bootstrap.it.stubs.PublicEventRawStorageReaderStub;
import com.onlydust.marketplace.indexer.bootstrap.it.stubs.TaskExecutorStub;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
@EnableConfigurationProperties
public class ApplicationIT {
    public static void main(String[] args) {
        SpringApplication.run(MarketplaceIndexerApplication.class, args);
    }

    @Bean
    @Primary
    TaskExecutor taskExecutor() {
        return new TaskExecutorStub();
    }

    @Bean
    @Primary
    PublicEventRawStorageReader publicEventRawStorageReader() {
        return new PublicEventRawStorageReaderStub();
    }
}
