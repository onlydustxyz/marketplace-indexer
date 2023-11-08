package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;

import java.util.Optional;

public class GithubAppContextStub implements GithubAppContext {
    @Override
    public void withGithubApp(Long installationId, Runnable callback) {
        callback.run();
    }

    @Override
    public Optional<Long> installationId() {
        return Optional.empty();
    }
}
