package com.onlydust.marketplace.indexer.github;

import java.net.http.HttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import com.onlydust.marketplace.indexer.github.adapters.GithubAppContextAdapter;
import com.onlydust.marketplace.indexer.github.adapters.GithubAppJwtTokenProvider;

import onlydust.com.marketplace.kernel.infrastructure.github.GithubAppJwtBuilder;

@Configuration
public class GithubConfiguration {
    private static class UnicodeSpecialCharHandler extends JsonNodeFactory {
        @Override
        public TextNode textNode(String text) {
            return super.textNode(Sanitizer.sanitize(text));
        }
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
            .findAndAddModules()
            .nodeFactory(new UnicodeSpecialCharHandler())
            .build();
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
                                             final Fetcher githubHttpFetcher,
                                             final GithubConfig githubConfig,
                                             final GithubTokenProvider tokenProvider) {
        return new GithubHttpClient(objectMapper, githubHttpFetcher, githubConfig, tokenProvider);
    }

    @Bean
    public GithubHttpClient githubAppHttpClient(final ObjectMapper objectMapper,
                                                final Fetcher githubAppHttpFetcher,
                                                final GithubConfig githubConfigForApp,
                                                final GithubAppJwtTokenProvider githubAppJwtTokenProvider) {
        return new GithubHttpClient(objectMapper, githubAppHttpFetcher, githubConfigForApp, githubAppJwtTokenProvider);
    }

    @Bean
    public GithubAppContextAdapter githubAppContextAdapter(final GithubHttpClient githubAppHttpClient) {
        return new GithubAppContextAdapter(githubAppHttpClient);
    }

    @Bean
    public GithubAppJwtTokenProvider githubAppJwtTokenProvider(final GithubAppJwtBuilder githubAppJwtBuilder) {
        return new GithubAppJwtTokenProvider(githubAppJwtBuilder);
    }

    @Bean
    public GithubAppJwtBuilder githubAppJwtBuilder(final GithubAppJwtBuilder.Config githubAppConfig) {
        return new GithubAppJwtBuilder(githubAppConfig);
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

    @Bean
    public Fetcher githubHttpFetcher(final HttpClient httpClient,
                                     final GithubConfig githubConfig) {
        return new RetriedOnErrorHttpFetcher(
                new RedirectionHandledHttpFetcher(
                        new RetriedOnErrorHttpFetcher(
                                new HttpFetcher(httpClient)),
                        githubConfig
                ));
    }

    @Bean
    public Fetcher githubAppHttpFetcher(final HttpClient httpClient,
                                        final GithubConfig githubConfigForApp) {
        return new RetriedOnErrorHttpFetcher(
                new RedirectionHandledHttpFetcher(
                        new RetriedOnErrorHttpFetcher(
                                new HttpFetcher(httpClient)),
                        githubConfigForApp
                ));
    }
}
