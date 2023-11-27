package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface RepoIndexingJobStorage {
    Set<Long> installationIds();

    Set<Long> reposUpdatedBefore(Long installationId, Instant since);

    void deleteInstallation(Long installationId);

    void deleteInstallationForRepos(Long installationId, List<Long> repoIds);

    void setSuspendedAt(Long installationId, Date suspendedAt);

    void startJob(Long repoId);

    void failJob(Long repoId);

    void endJob(Long repoId);

    void configureRepoForFullIndexing(Long repoId);

    void setInstallationForRepos(Long installationId, Long... repoIds);
}
