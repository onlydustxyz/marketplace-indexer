package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.rest.api.authentication.WebSecurityAdapter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSecurityConfiguration {
    @Bean
    WebSecurityAdapter webSecurityAdapter() {
        return new WebSecurityAdapter();
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
