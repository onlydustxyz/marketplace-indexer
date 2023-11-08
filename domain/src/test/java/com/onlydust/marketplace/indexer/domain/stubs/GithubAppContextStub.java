package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;

public class GithubAppContextStub implements GithubAppContext {
    @Override
    public void withGithubApp(Long installationId, Runnable callback) {
        callback.run();
    }
}
