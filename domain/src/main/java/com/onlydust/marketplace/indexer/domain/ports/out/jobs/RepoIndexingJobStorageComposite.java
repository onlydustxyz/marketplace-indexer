package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class RepoIndexingJobStorageComposite implements RepoIndexingJobStorage {
    private final RepoIndexingJobStorage[] repoIndexingJobRepositories;

    public RepoIndexingJobStorageComposite(RepoIndexingJobStorage... repoIndexingJobRepositories) {
        this.repoIndexingJobRepositories = repoIndexingJobRepositories;
    }

    @Override
    public Set<Long> installationIds() {
        return Arrays.stream(repoIndexingJobRepositories)
                .flatMap(repoIndexingJobRepository -> repoIndexingJobRepository.installationIds().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> repos(Long installationId) {
        return Arrays.stream(repoIndexingJobRepositories)
                .flatMap(repoIndexingJobRepository -> repoIndexingJobRepository.repos(installationId).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public void add(Long installationId, Long... repoIds) {
        Arrays.stream(repoIndexingJobRepositories)
                .forEach(repoIndexingJobRepository -> repoIndexingJobRepository.add(installationId, repoIds));
    }

    @Override
    public void deleteAll(Long installationId) {
        Arrays.stream(repoIndexingJobRepositories)
                .forEach(repoIndexingJobRepository -> repoIndexingJobRepository.deleteAll(installationId));
    }
}
