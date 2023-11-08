package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import java.util.Set;

public interface RepoRefresher {
    void refreshRepos(Long installationId, Set<Long> repoIds);
}
