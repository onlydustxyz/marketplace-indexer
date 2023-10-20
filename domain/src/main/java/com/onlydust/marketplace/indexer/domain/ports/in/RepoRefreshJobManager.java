package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.jobs.Job;

import java.util.List;

public interface RepoRefreshJobManager {
    void addRepoToRefresh(Long repoId);

    List<Job> allJobs();
}
