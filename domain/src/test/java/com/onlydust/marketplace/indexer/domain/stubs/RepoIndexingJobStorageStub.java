package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RepoIndexingJobStorageStub implements RepoIndexingJobStorage {
    private final Map<Long, Set<Long>> triggers = new HashMap<>();

    public void feedWith(Map<Long, Set<Long>> triggers) {
        this.triggers.putAll(triggers);
    }

    @Override
    public Set<Long> installationIds() {
        return triggers.keySet();
    }

    @Override
    public Set<Long> repos(Long installationId) {
        return triggers.get(installationId);
    }

    @Override
    public void deleteAll(Long installationId) {
        triggers.remove(installationId);
    }

    @Override
    public void add(Long installationId, Long... repoIds) {
        final var repos = triggers.getOrDefault(installationId, new HashSet<>());
        repos.addAll(Set.of(repoIds));
        triggers.put(installationId, repos);
    }
}
