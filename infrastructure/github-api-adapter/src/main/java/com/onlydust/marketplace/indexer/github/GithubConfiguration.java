package com.onlydust.marketplace.indexer.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.github.adapters.GithubAppContextAdapter;
import com.onlydust.marketplace.indexer.github.adapters.GithubAppJwtProvider;
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
    public GithubHttpClient githubAppHttpClient(final ObjectMapper objectMapper,
                                                final HttpClient httpClient,
                                                final GithubConfig config,
                                                final GithubAppJwtProvider githubAppJwtProvider) {
        return new GithubHttpClient(objectMapper, httpClient, config, githubAppJwtProvider);
    }

    @Bean
    public GithubAppContextAdapter githubAppContextAdapter(final GithubHttpClient githubAppHttpClient) {
        return new GithubAppContextAdapter(githubAppHttpClient);
    }

    @Bean
    public GithubAppJwtProvider githubAppJwtProvider(final GithubAppJwtProvider.Config githubAppConfig) {
        return new GithubAppJwtProvider(githubAppConfig);
    }


    @Bean
    public GithubTokenProvider tokenProvider(final GithubConfig githubConfig,
                                             final GithubAppContextAdapter githubAppContextAdapter) {
        return new GithubTokenProviderComposite(
                githubAppContextAdapter,
                new GithubAuthorizationContext(),
                new DefaultGithubAccessTokenProvider(githubConfig));
    }
}
