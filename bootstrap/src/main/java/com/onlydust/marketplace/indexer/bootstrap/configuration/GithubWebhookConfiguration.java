package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.services.EventProcessorService;
import com.onlydust.marketplace.indexer.rest.github.GithubWebhookRestApi;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GithubWebhookConfiguration {
    @Bean
    @ConfigurationProperties("application.web.github.webhook")
    public GithubWebhookRestApi.Config githubWebhookConfig() {
        return new GithubWebhookRestApi.Config();
    }

    @Bean
    public GithubWebhookRestApi githubWebhookRestApi(final ObjectMapper objectMapper, final GithubWebhookRestApi.Config githubWebhookConfig, final EventProcessorService eventProcessorService) {
        return new GithubWebhookRestApi(objectMapper, githubWebhookConfig, eventProcessorService);
    }
}
