package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobRepository;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobTriggerEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobTriggerEntityRepository;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Set;

@AllArgsConstructor
public class PostgresRepoIndexingJobRepository implements RepoIndexingJobRepository {
    private final RepoIndexingJobTriggerEntityRepository repoIndexingJobTriggerRepository;

    @Override
    public Set<Long> installationIds() {
        return repoIndexingJobTriggerRepository.listDistinctInstallationIds();
    }

    @Override
    public Set<Long> repos(Long installationId) {
        return repoIndexingJobTriggerRepository.findDistinctRepoIdsByInstallationId(installationId);
    }

    @Override
    public void add(Long installationId, Long... repoIds) {
        repoIndexingJobTriggerRepository.saveAll(Arrays.stream(repoIds).map(repoId -> new RepoIndexingJobTriggerEntity(repoId, installationId)).toList());
    }

    @Override
    public void deleteAll(Long installationId) {
        repoIndexingJobTriggerRepository.deleteAllByInstallationId(installationId);
    }

}