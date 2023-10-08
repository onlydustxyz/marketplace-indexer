package com.onlydust.marketplace.indexer.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.github.adapters.GithubRawStorageReader;
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
    public GithubRawStorageReader githubRawStorageReader(final ObjectMapper objectMapper, final HttpClient httpClient) {
        return new GithubRawStorageReader(new GithubHttpClient(objectMapper, httpClient, "https://api.github.com"));
    }
}
