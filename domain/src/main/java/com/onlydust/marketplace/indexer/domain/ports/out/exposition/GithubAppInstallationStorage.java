package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface GithubAppInstallationStorage {
    void save(GithubAppInstallation installation);

    void addRepos(Long installationId, List<GithubRepo> repos);

    void delete(Long installationId);

    void removeRepos(Long installationId, List<Long> repoIds);

    void setSuspendedAt(Long installationId, Date suspendedAt);

    Optional<Long> findInstallationIdByAccount(Long accountId);
}
