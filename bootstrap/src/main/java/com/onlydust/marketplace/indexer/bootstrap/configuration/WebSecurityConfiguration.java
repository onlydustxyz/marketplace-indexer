package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.rest.api.authentication.DelegatedAuthenticationEntryPoint;
import com.onlydust.marketplace.indexer.rest.api.authentication.api_key.ApiKeyAuthenticationFilter;
import com.onlydust.marketplace.indexer.rest.api.authentication.api_key.ApiKeyAuthenticationService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@Profile("api")
public class WebSecurityConfiguration {
    @Bean
    AuthenticationEntryPoint authenticationEntryPoint(final HandlerExceptionResolver handlerExceptionResolver) {
        return new DelegatedAuthenticationEntryPoint(handlerExceptionResolver);
    }

    @Bean
    @ConfigurationProperties("application.web.authentication")
    ApiKeyAuthenticationService.Config authenticationConfig() {
        return new ApiKeyAuthenticationService.Config();
    }

    @Bean
    ApiKeyAuthenticationService apiKeyAuthenticationService(final ApiKeyAuthenticationService.Config authenticationConfig) {
        return new ApiKeyAuthenticationService(authenticationConfig);
    }

    @Bean
    ApiKeyAuthenticationFilter apiKeyAuthenticationFilter(final ApiKeyAuthenticationService apiKeyAuthenticationService) {
        return new ApiKeyAuthenticationFilter(apiKeyAuthenticationService);
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

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter,
                                           final AuthenticationEntryPoint authEntryPoint) throws Exception {
        http
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers(antMatcher(HttpMethod.POST, "/github-app/**")).permitAll()
                                .requestMatchers(antMatcher(HttpMethod.GET, "/swagger-ui.html")).permitAll()
                                .requestMatchers(antMatcher(HttpMethod.GET, "/v3/api-docs/**")).permitAll()
                                .requestMatchers(antMatcher(HttpMethod.GET, "/swagger-ui/**")).permitAll()
                                .requestMatchers(antMatcher(HttpMethod.GET, "/actuator/health")).permitAll()
                                .requestMatchers(antMatcher(HttpMethod.GET, "/")).permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        (exceptionHandling) -> exceptionHandling.authenticationEntryPoint(authEntryPoint)
                );
        return http.build();
    }

}
