package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import java.time.ZonedDateTime;
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
    public Set<Long> installationIds() {
        return Arrays.stream(repoIndexingJobRepositories)
                .flatMap(repoIndexingJobRepository -> repoIndexingJobRepository.installationIds().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Long> reposUpdatedBefore(Long installationId, ZonedDateTime since) {
        return Arrays.stream(repoIndexingJobRepositories)
                .flatMap(repoIndexingJobRepository -> repoIndexingJobRepository.reposUpdatedBefore(installationId, since).stream())
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
    public void setSuspendedAt(Long installationId, ZonedDateTime suspendedAt) {
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
