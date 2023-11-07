package com.onlydust.marketplace.indexer.github;

import lombok.AllArgsConstructor;

import java.util.Optional;


@AllArgsConstructor
public class DefaultGithubAccessTokenProvider implements GithubTokenProvider {
    final GithubConfig config;

    @Override
    public Optional<String> accessToken() {
        return Optional.ofNullable(config.getPersonalAccessToken());
    }
}
