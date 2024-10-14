package com.onlydust.marketplace.indexer.domain.ports.in.jobs;

import java.util.Set;

public interface RepoIndexingJobScheduler {
    void addReposToRefresh(Set<Long> repoIds);

    void removeReposToRefresh(Set<Long> repoIds);
}
