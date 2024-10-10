package com.onlydust.marketplace.indexer.bootstrap;

import com.onlydust.marketplace.indexer.bootstrap.it.stubs.PublicEventRawStorageReaderStub;
import com.onlydust.marketplace.indexer.bootstrap.it.stubs.TaskExecutorStub;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.CacheWritePublicEventRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import com.onlydust.marketplace.indexer.domain.services.readers.PublicEventRawStorageReaderAggregator;
import com.onlydust.marketplace.indexer.infrastructure.github_archives.GithubArchivesClient;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRawStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;

import static org.mockito.Mockito.mock;

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
    PublicEventRawStorageReader publicEventRawStorageReaderStub(PublicEventRawStorageReaderStub githubArchivesReaderStub,
                                                                PublicEventRawStorageReaderStub githubApiReaderStub,
                                                                PostgresRawStorage postgresRawStorage) {
        return CacheWritePublicEventRawStorageReaderDecorator.builder()
                .fetcher(new PublicEventRawStorageReaderAggregator(githubArchivesReaderStub, githubApiReaderStub))
                .cache(postgresRawStorage)
                .build();
    }

    @Bean
    PublicEventRawStorageReaderStub githubArchivesReaderStub() {
        return new PublicEventRawStorageReaderStub();
    }

    @Bean
    PublicEventRawStorageReaderStub githubApiReaderStub() {
        return new PublicEventRawStorageReaderStub();
    }

    @Bean
    @Primary
    GithubArchivesClient githubArchivesClientMock() {
        return mock(GithubArchivesClient.class);
    }
}
