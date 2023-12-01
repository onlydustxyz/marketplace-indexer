package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepositoryEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.rest.github.GithubWebhookRestApi;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("github")
public class GithubWebhookConfiguration {
    @Bean
    @ConfigurationProperties("application.web.github.webhook")
    public GithubWebhookRestApi.Config githubWebhookConfig() {
        return new GithubWebhookRestApi.Config();
    }

    @Bean
    public GithubWebhookRestApi githubWebhookRestApi(final ObjectMapper objectMapper,
                                                     final GithubWebhookRestApi.Config githubWebhookConfig,
                                                     final EventHandler<RawInstallationEvent> installationEventHandler,
                                                     final EventHandler<RawRepositoryEvent> repositoryEventHandler) {
        return new GithubWebhookRestApi(objectMapper, githubWebhookConfig, installationEventHandler, repositoryEventHandler);
    }
}
