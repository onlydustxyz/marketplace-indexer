package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.ports.out.CacheWriteRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import com.onlydust.marketplace.indexer.github.adapters.GithubRawStorageReader;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRawStorageRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {
    @Bean
    public IndexingService onDemandIndexer(final GithubRawStorageReader githubRawStorageReader, final PostgresRawStorageRepository postgresRawStorageRepository) {
        final var rawStorageReader = new CacheWriteRawStorageReaderDecorator(githubRawStorageReader, postgresRawStorageRepository);
        return new IndexingService(rawStorageReader);
    }
}
