package com.onlydust.marketplace.indexer.github;

import java.util.Optional;

public interface GithubTokenProvider {
    Optional<String> accessToken();
}
