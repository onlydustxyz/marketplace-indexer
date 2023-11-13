package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.entities.OldRepoIndexesEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.OldRepoIndexesEntityRepository;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class PostgresOldRepoIndexingJobStorage implements RepoIndexingJobStorage {
    private final OldRepoIndexesEntityRepository oldRepoIndexesEntityRepository;

    @Override
    public Set<Long> installationIds() {
        return Set.of();
    }

    @Override
    public Set<Long> repos(Long installationId) {
        return Set.of();
    }

    @Override
    public void add(Long installationId, Long... repoIds) {
        oldRepoIndexesEntityRepository.saveAll(Arrays.stream(repoIds).map(OldRepoIndexesEntity::new).toList());
    }

    @Override
    public void deleteInstallation(Long installationId) {
    }

    @Override
    public void deleteInstallationForRepos(Long installationId, List<Long> repoIds) {
    }

    @Override
    public void setSuspendedAt(Long installationId, Instant suspendedAt) {
    }

    @Override
    public void startJob(Long repoId) {
    }

    @Override
    public void failJob(Long repoId) {
    }

    @Override
    public void endJob(Long repoId) {
    }
}
