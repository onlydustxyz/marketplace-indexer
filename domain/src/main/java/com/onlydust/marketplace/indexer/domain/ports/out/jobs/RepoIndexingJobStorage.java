package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import java.util.List;
import java.util.Set;

public interface RepoIndexingJobStorage {
    Set<Long> installationIds();

    Set<Long> repos(Long installationId);

    void add(Long installationId, Long... repoIds);

    void deleteInstallation(Long installationId);

    void deleteInstallationForRepos(Long installationId, List<Long> repoIds);
}
