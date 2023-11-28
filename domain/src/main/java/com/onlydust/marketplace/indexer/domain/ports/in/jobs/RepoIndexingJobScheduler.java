package com.onlydust.marketplace.indexer.domain.ports.in.jobs;

import java.util.List;

public interface RepoIndexingJobScheduler {
    void addReposToRefresh(List<Long> repoIds);

    void removeReposToRefresh(List<Long> repoIds);
}
