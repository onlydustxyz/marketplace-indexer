package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface RepoIndexingJobStorage {
    Set<Long> installationIds();

    Set<RepoIndexingJobTrigger> reposUpdatedBefore(Long installationId, Instant since);

    void deleteInstallation(Long installationId);

    void deleteInstallationForRepos(Long installationId, List<Long> repoIds);

    void setSuspendedAt(Long installationId, Date suspendedAt);

    void startJob(Long repoId);

    void failJob(Long repoId);

    void endJob(Long repoId);

    void configureRepoForFullIndexing(Long repoId, Boolean isPublic);

    void setInstallationForRepos(Long installationId, RepoIndexingJobTrigger... triggers);

    void setPrivate(Long repoId);

    void setPublic(Long repoId);

    void configureRepoForLightIndexing(Long repoId);
}
