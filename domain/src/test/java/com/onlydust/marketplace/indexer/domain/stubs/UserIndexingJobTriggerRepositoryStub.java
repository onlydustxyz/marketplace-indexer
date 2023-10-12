package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.UserIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.UserIndexingJobTriggerRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserIndexingJobTriggerRepositoryStub implements UserIndexingJobTriggerRepository {
    private final List<UserIndexingJobTrigger> triggers = new ArrayList<>();

    @Override
    public List<UserIndexingJobTrigger> list() {
        return triggers;
    }

    public void feedWith(UserIndexingJobTrigger... triggers) {
        this.triggers.addAll(Arrays.stream(triggers).toList());
    }
}
