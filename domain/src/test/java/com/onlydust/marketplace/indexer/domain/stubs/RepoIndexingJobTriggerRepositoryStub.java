package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobTriggerRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepoIndexingJobTriggerRepositoryStub implements RepoIndexingJobTriggerRepository {
    private final List<RepoIndexingJobTrigger> triggers = new ArrayList<>();

    @Override
    public void add(RepoIndexingJobTrigger trigger) {
        triggers.add(trigger);
    }

    @Override
    public List<RepoIndexingJobTrigger> list() {
        return triggers;
    }

    public void feedWith(RepoIndexingJobTrigger... triggers) {
        Arrays.stream(triggers).forEach(this::add);
    }
}
