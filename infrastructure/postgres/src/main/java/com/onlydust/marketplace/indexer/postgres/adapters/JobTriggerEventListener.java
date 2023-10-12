package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.EventListener;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobTriggerEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobTriggerEntityRepository;
import lombok.AllArgsConstructor;

import javax.transaction.Transactional;

@AllArgsConstructor
@Transactional
public class JobTriggerEventListener implements EventListener<InstallationEvent> {
    private final RepoIndexingJobTriggerEntityRepository repoIndexingJobTriggerRepository;

    @Override
    public void onEvent(InstallationEvent event) {
        repoIndexingJobTriggerRepository.deleteAllByInstallationId(event.getInstallationId());
        repoIndexingJobTriggerRepository.saveAll(event.getRepos().stream()
                .map(repo -> RepoIndexingJobTriggerEntity.builder()
                        .repoId(repo.id())
                        .installationId(event.getInstallationId())
                        .build())
                .toList());
    }
}
