package com.onlydust.marketplace.indexer.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class GithubConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public DefaultGithubAccessTokenProvider onlyDustGithubAccessTokenProvider(final GithubConfig githubConfig) {
        return new DefaultGithubAccessTokenProvider(githubConfig);
    }

    @Bean
    public GithubAuthorizationContext githubAuthorizationContext() {
        return new GithubAuthorizationContext();
    }

    @Bean
    public GithubHttpClient githubHttpClient(final ObjectMapper objectMapper,
                                             final HttpClient httpClient,
                                             final GithubConfig config,
                                             final GithubTokenProvider tokenProvider) {
        return new GithubHttpClient(objectMapper, httpClient, config, tokenProvider);
    }

    @Bean
    public GithubTokenProvider tokenProvider(final GithubConfig githubConfig) {
        return new GithubTokenProviderComposite(
                new GithubAuthorizationContext(),
                new DefaultGithubAccessTokenProvider(githubConfig));
    }
}
