package com.onlydust.marketplace.indexer.domain.ports.out;

import java.util.Set;

public interface UserIndexingJobRepository {
    Set<Long> users();

    void add(Long userId);
}
