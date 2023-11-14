package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class RepoIndexingJobStorageComposite implements RepoIndexingJobStorage {
    private final RepoIndexingJobStorage[] repoIndexingJobRepositories;

    public RepoIndexingJobStorageComposite(RepoIndexingJobStorage... repoIndexingJobRepositories) {
        this.repoIndexingJobRepositories = repoIndexingJobRepositories;
    }

    @Override
    public Set<Long> installationIds(Instant since) {
        return Arrays.stream(repoIndexingJobRepositories)
                .flatMap(repoIndexingJobRepository -> repoIndexingJobRepository.installationIds(since).stream())
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
    public void deleteInstallation(Long installationId) {
        Arrays.stream(repoIndexingJobRepositories)
                .forEach(repoIndexingJobRepository -> repoIndexingJobRepository.deleteInstallation(installationId));
    }

    @Override
    public void deleteInstallationForRepos(Long installationId, List<Long> repoIds) {
        Arrays.stream(repoIndexingJobRepositories)
                .forEach(repoIndexingJobRepository -> repoIndexingJobRepository.deleteInstallationForRepos(installationId, repoIds));
    }

    @Override
    public void setSuspendedAt(Long installationId, Instant suspendedAt) {
        Arrays.stream(repoIndexingJobRepositories)
                .forEach(repoIndexingJobRepository -> repoIndexingJobRepository.setSuspendedAt(installationId, suspendedAt));
    }

    @Override
    public void startJob(Long repoId) {
        Arrays.stream(repoIndexingJobRepositories)
                .forEach(repoIndexingJobRepository -> repoIndexingJobRepository.startJob(repoId));
    }

    @Override
    public void failJob(Long repoId) {
        Arrays.stream(repoIndexingJobRepositories)
                .forEach(repoIndexingJobRepository -> repoIndexingJobRepository.failJob(repoId));
    }

    @Override
    public void endJob(Long repoId) {
        Arrays.stream(repoIndexingJobRepositories)
                .forEach(repoIndexingJobRepository -> repoIndexingJobRepository.endJob(repoId));
    }
}
