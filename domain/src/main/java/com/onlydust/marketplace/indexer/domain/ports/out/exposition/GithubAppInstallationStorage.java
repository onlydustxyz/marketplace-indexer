package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;

public interface GithubAppInstallationStorage {
    void save(GithubAppInstallation installation);

    void delete(Long installationId);
}
