package com.onlydust.marketplace.indexer.domain.ports.in;

public interface RepoRefreshJobManager {
    void addRepoToRefresh(Long repoId);

    void runAllJobs();
}
