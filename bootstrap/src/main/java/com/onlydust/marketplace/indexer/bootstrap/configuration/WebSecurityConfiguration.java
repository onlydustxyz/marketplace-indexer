package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.rest.api.authentication.WebSecurityAdapter;
import com.onlydust.marketplace.indexer.rest.api.authentication.api_key.ApiKeyAuthenticationFilter;
import com.onlydust.marketplace.indexer.rest.api.authentication.api_key.ApiKeyAuthenticationService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSecurityConfiguration {
    @Bean
    ApiKeyAuthenticationService apiKeyAuthenticationService() {
        return new ApiKeyAuthenticationService();
    }

    @Bean
    ApiKeyAuthenticationFilter apiKeyAuthenticationFilter(final ApiKeyAuthenticationService apiKeyAuthenticationService) {
        return new ApiKeyAuthenticationFilter(apiKeyAuthenticationService);
    }

    @Bean
    WebSecurityAdapter webSecurityAdapter(final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter) {
        return new WebSecurityAdapter(apiKeyAuthenticationFilter);
    }

    @Bean
    @ConfigurationProperties("application.web.cors")
    public WebCorsProperties webCorsProperties() {
        return new WebCorsProperties();
    }

    @Data
    public static class WebCorsProperties {
        private String[] hosts;
    }
}
