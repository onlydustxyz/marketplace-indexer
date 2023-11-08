package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import java.util.Set;

public interface UserRefresher {
    void refreshUsers(Set<Long> userIds);
}
