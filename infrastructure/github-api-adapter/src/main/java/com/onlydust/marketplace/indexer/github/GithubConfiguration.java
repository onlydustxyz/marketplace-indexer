package com.onlydust.marketplace.indexer.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.onlydust.marketplace.indexer.github.adapters.GithubAppContextAdapter;
import com.onlydust.marketplace.indexer.github.adapters.GithubAppJwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class GithubConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
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
                                             final GithubConfig githubConfig,
                                             final GithubTokenProvider tokenProvider) {
        return new GithubHttpClient(objectMapper, httpClient, githubConfig, tokenProvider);
    }


    @Bean
    public GithubHttpClient githubAppHttpClient(final ObjectMapper objectMapper,
                                                final HttpClient httpClient,
                                                final GithubConfig githubConfigForApp,
                                                final GithubAppJwtProvider githubAppJwtProvider) {
        return new GithubHttpClient(objectMapper, httpClient, githubConfigForApp, githubAppJwtProvider);
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
                                             final GithubAuthorizationContext githubAuthorizationContext,
                                             final GithubAppContextAdapter githubAppContextAdapter) {
        return new GithubTokenProviderComposite(
                githubAppContextAdapter,
                githubAuthorizationContext,
                new DefaultGithubAccessTokenProvider(githubConfig));
    }
}
