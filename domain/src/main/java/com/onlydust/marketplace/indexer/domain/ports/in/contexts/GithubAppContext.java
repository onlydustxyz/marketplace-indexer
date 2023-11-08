package com.onlydust.marketplace.indexer.domain.ports.in.contexts;

public interface GithubAppContext {
    void withGithubApp(Long installationId, Runnable callback);
}
