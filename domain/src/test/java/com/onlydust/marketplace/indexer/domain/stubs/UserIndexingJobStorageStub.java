package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UserIndexingJobStorageStub implements UserIndexingJobStorage {
    private final Set<Long> userIds = new HashSet<>();


    @Override
    public Set<Long> users() {
        return userIds;
    }

    @Override
    public void add(Long userId) {
        userIds.add(userId);
    }

    public void feedWith(Long... userIds) {
        this.userIds.addAll(Arrays.asList(userIds));
    }
}
