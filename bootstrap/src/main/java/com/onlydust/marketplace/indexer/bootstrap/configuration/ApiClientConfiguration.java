package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.api.client.ApiHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiClientConfiguration {
    @Bean
    @ConfigurationProperties(value = "infrastructure.api-client")
    public ApiHttpClient.Config apiHttpClientConfig() {
        return new ApiHttpClient.Config();
    }

    @Bean
    public ApiHttpClient apiHttpClient(final ApiHttpClient.Config apiHttpClientConfig) {
        return new ApiHttpClient(apiHttpClientConfig);
    }
}
