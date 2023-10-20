package com.onlydust.marketplace.indexer.domain.ports.out;

public interface GithubAccountRepository {
    void removeInstallation(Long id);
}
