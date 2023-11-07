package com.onlydust.marketplace.indexer.github;

import java.util.Arrays;
import java.util.Optional;

public class GithubTokenProviderComposite implements GithubTokenProvider {
    private final GithubTokenProvider[] providers;

    public GithubTokenProviderComposite(GithubTokenProvider... providers) {
        this.providers = providers;
    }

    @Override
    public Optional<String> accessToken() {
        return Arrays.stream(providers)
                .map(GithubTokenProvider::accessToken)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
