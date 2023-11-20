package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.ports.in.events.InstallationEventHandler;
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
                                                     final InstallationEventHandler installationEventHandler) {
        return new GithubWebhookRestApi(objectMapper, githubWebhookConfig, installationEventHandler);
    }
}
