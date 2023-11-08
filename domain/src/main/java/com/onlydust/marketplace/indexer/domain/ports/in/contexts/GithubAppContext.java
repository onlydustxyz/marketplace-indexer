package com.onlydust.marketplace.indexer.domain.ports.in.contexts;

import java.util.Optional;

public interface GithubAppContext {
    void withGithubApp(Long installationId, Runnable callback);

    Optional<Long> installationId();
}
