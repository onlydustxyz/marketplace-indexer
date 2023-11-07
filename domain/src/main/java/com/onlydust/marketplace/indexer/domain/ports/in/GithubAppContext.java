package com.onlydust.marketplace.indexer.domain.ports.in;

public interface GithubAppContext {
    void withGithubApp(Long installationId, Runnable callback);
}
