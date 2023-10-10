package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.ports.out.CacheWriteRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.services.EventProcessorService;
import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import com.onlydust.marketplace.indexer.github.adapters.GithubRawStorageReader;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresInstallationEventListener;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRawInstallationEventStorageRepository;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRawStorageRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class DomainConfiguration {
    @Bean
    @ConfigurationProperties("infrastructure.github")
    GithubHttpClient.Config githubConfig() {
        return new GithubHttpClient.Config();
    }

    @Bean
    public EventProcessorService eventProcessorService(final PostgresRawInstallationEventStorageRepository postgresRawInstallationEventStorageRepository,
                                                       final PostgresInstallationEventListener postgresInstallationEventListener,
                                                       final GithubRawStorageReader githubRawStorageReader,
                                                       final PostgresRawStorageRepository postgresRawStorageRepository) {
        final var rawStorageReader = new CacheWriteRawStorageReaderDecorator(githubRawStorageReader, postgresRawStorageRepository);
        return new EventProcessorService(postgresRawInstallationEventStorageRepository, postgresInstallationEventListener, rawStorageReader);
    }

    @Bean
    public GithubHttpClient githubHttpClient(final ObjectMapper objectMapper, final HttpClient httpClient, final GithubHttpClient.Config config) {
        return new GithubHttpClient(objectMapper, httpClient, config);
    }

    @Bean
    GithubRawStorageReader githubRawStorageReader(final GithubHttpClient githubHttpClient) {
        return new GithubRawStorageReader(githubHttpClient);
    }

    @Bean
    public IndexingService onDemandIndexer(final GithubRawStorageReader githubRawStorageReader, final PostgresRawStorageRepository postgresRawStorageRepository) {
        final var rawStorageReader = new CacheWriteRawStorageReaderDecorator(githubRawStorageReader, postgresRawStorageRepository);
        return new IndexingService(rawStorageReader);
    }
}
