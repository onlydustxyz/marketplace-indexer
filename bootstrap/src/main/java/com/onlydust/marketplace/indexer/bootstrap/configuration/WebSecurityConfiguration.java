package com.onlydust.marketplace.indexer.bootstrap.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSecurityConfiguration {
    @Bean
    public com.onlydust.marketplace.indexer.rest.api.authentication.WebSecurityConfiguration apiSecurityConfiguration() {
        return new com.onlydust.marketplace.indexer.rest.api.authentication.WebSecurityConfiguration();
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
