package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.*;
import com.onlydust.marketplace.indexer.domain.services.EventProcessorService;
import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import com.onlydust.marketplace.indexer.github.adapters.GithubRawStorageReader;
import com.onlydust.marketplace.indexer.postgres.adapters.JobTriggerEventListener;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresInstallationEventListener;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRawInstallationEventStorageRepository;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRawStorageRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.util.List;

@Configuration
public class DomainConfiguration {
    @Bean
    @ConfigurationProperties("infrastructure.github")
    GithubHttpClient.Config githubConfig() {
        return new GithubHttpClient.Config();
    }

    @Bean
    RawStorageReader cachedRawStorageReader(
            final GithubRawStorageReader githubRawStorageReader,
            final PostgresRawStorageRepository postgresRawStorageRepository
    ) {
        return CacheReadRawStorageReaderDecorator.builder()
                .fetcher(CacheWriteRawStorageReaderDecorator.builder()
                        .fetcher(githubRawStorageReader)
                        .cache(postgresRawStorageRepository)
                        .build())
                .cache(postgresRawStorageRepository)
                .build();
    }

    @Bean(name = "installationEventEventListener")
    public EventListener<InstallationEvent> installationEventEventListener(
            final PostgresInstallationEventListener postgresInstallationEventListener,
            final JobTriggerEventListener jobTriggerEventListener) {
        return new EventListenerComposite<>(List.of(postgresInstallationEventListener, jobTriggerEventListener));
    }

    @Bean
    public EventProcessorService eventProcessorService(final PostgresRawInstallationEventStorageRepository postgresRawInstallationEventStorageRepository,
                                                       @Qualifier("installationEventEventListener") final EventListener<InstallationEvent> eventListener,
                                                       final RawStorageReader cachedRawStorageReader) {
        return new EventProcessorService(postgresRawInstallationEventStorageRepository, eventListener, cachedRawStorageReader);
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
    public IndexingService onDemandIndexer(final RawStorageReader cachedRawStorageReader) {
        return new IndexingService(cachedRawStorageReader);
    }
}
