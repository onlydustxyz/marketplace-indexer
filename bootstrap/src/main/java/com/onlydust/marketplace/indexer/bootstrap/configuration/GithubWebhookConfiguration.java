package com.onlydust.marketplace.indexer.bootstrap.configuration;

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
}
