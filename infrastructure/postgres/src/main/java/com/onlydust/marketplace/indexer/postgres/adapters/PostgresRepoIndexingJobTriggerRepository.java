package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobTriggerRepository;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobTriggerEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobTriggerEntityRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class PostgresRepoIndexingJobTriggerRepository implements RepoIndexingJobTriggerRepository {
    private final RepoIndexingJobTriggerEntityRepository repoIndexingJobTriggerRepository;

    @Override
    public void add(RepoIndexingJobTrigger trigger) {
        repoIndexingJobTriggerRepository.save(RepoIndexingJobTriggerEntity.of(trigger));
    }

    @Override
    public List<RepoIndexingJobTrigger> list() {
        return repoIndexingJobTriggerRepository.findAll().stream()
                .map(trigger -> new RepoIndexingJobTrigger(
                        Optional.ofNullable(trigger.getInstallationId()).orElse(0L),
                        trigger.getRepoId()))
                .toList();
    }
}
