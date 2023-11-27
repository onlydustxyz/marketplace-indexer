package com.onlydust.marketplace.indexer.domain.ports.in.jobs;

public interface RepoIndexingJobScheduler {
    void addRepoToRefresh(Long repoId);

    void removeRepoToRefresh(Long repoId);
}
