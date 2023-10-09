package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.rest.api.authentication.DelegatedAuthenticationEntryPoint;
import com.onlydust.marketplace.indexer.rest.api.authentication.WebSecurityAdapter;
import com.onlydust.marketplace.indexer.rest.api.authentication.api_key.ApiKeyAuthenticationFilter;
import com.onlydust.marketplace.indexer.rest.api.authentication.api_key.ApiKeyAuthenticationService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class WebSecurityConfiguration {
    @Bean
    AuthenticationEntryPoint authenticationEntryPoint(final HandlerExceptionResolver handlerExceptionResolver) {
        return new DelegatedAuthenticationEntryPoint(handlerExceptionResolver);
    }

    @Bean
    ApiKeyAuthenticationService apiKeyAuthenticationService() {
        return new ApiKeyAuthenticationService();
    }

    @Bean
    ApiKeyAuthenticationFilter apiKeyAuthenticationFilter(final ApiKeyAuthenticationService apiKeyAuthenticationService) {
        return new ApiKeyAuthenticationFilter(apiKeyAuthenticationService);
    }

    @Bean
    WebSecurityAdapter webSecurityAdapter(final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter, final AuthenticationEntryPoint authenticationEntryPoint) {
        return new WebSecurityAdapter(apiKeyAuthenticationFilter, authenticationEntryPoint);
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
