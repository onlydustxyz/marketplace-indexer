package com.onlydust.marketplace.indexer.domain.ports.out;

import java.util.Set;

public interface UserRefresher {
    void refreshUsers(Set<Long> userIds);
}
