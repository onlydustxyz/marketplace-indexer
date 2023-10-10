package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.rest.github.GithubWebhookRestApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GithubWebhookConfiguration {
    @Bean
    public GithubWebhookRestApi githubWebhookRestApi(final ObjectMapper objectMapper) {
        return new GithubWebhookRestApi(objectMapper);
    }
}
