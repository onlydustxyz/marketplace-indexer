package com.onlydust.marketplace.indexer.domain.ports.out;

import java.util.Set;

public interface RepoIndexingJobRepository {
    Set<Long> installationIds();

    Set<Long> repos(Long installationId);

    void add(Long installationId, Long... repoIds);

    void deleteAll(Long installationId);
}
