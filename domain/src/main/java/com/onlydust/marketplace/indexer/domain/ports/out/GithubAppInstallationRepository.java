package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;

public interface GithubAppInstallationRepository {
    void save(GithubAppInstallation installation);

    void delete(Long installationId);
}
