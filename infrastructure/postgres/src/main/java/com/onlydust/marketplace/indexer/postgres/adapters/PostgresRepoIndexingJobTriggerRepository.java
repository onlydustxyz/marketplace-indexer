package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobTriggerRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class PostgresRepoIndexingJobTriggerRepository implements com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobTriggerRepository {
    private final RepoIndexingJobTriggerRepository repoIndexingJobTriggerRepository;

    @Override
    public List<RepoIndexingJobTrigger> list() {
        return repoIndexingJobTriggerRepository.findAll().stream()
                .map(trigger -> new RepoIndexingJobTrigger(
                        Optional.ofNullable(trigger.getInstallationId()).orElse(0L),
                        trigger.getRepoId()))
                .toList();
    }
}
