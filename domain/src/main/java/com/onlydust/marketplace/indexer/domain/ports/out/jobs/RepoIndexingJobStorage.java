package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface RepoIndexingJobStorage {
    Set<Long> installationIds();

    Set<Long> reposUpdatedBefore(Long installationId, Instant since);

    void add(Long installationId, Long... repoIds);

    void deleteInstallation(Long installationId);

    void deleteInstallationForRepos(Long installationId, List<Long> repoIds);

    void setSuspendedAt(Long installationId, Instant suspendedAt);

    void startJob(Long repoId);

    void failJob(Long repoId);

    void endJob(Long repoId);
}
