package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.infrastructure.github_archives.GithubArchivesClient;
import com.onlydust.marketplace.indexer.infrastructure.github_archives.adapters.GithubArchivesPublicEventRawStorageReaderAdapter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GithubArchivesConfiguration {
    @Bean
    @ConfigurationProperties(value = "infrastructure.github-archives")
    public GithubArchivesClient.Properties githubArchivesProperties() {
        return new GithubArchivesClient.Properties();
    }

    @Bean
    public GithubArchivesClient githubArchivesClient(final GithubArchivesClient.Properties githubArchivesProperties) {
        return new GithubArchivesClient(githubArchivesProperties);
    }

    @Bean
    public GithubArchivesPublicEventRawStorageReaderAdapter githubArchivesPublicEventRawStorageReader(final GithubArchivesClient githubArchivesClient) {
        return new GithubArchivesPublicEventRawStorageReaderAdapter(githubArchivesClient);
    }
}
