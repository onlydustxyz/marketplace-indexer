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
        switch (event.getAction()) {
            case CREATED -> onCreated(event);
            case DELETED -> onDeleted(event);
        }

    }

    private void onCreated(InstallationEvent event) {
        repoIndexingJobTriggerRepository.saveAll(event.getRepos().stream()
                .map(repo -> RepoIndexingJobTriggerEntity.builder()
                        .repoId(repo.getId())
                        .installationId(event.getInstallationId())
                        .build())
                .toList());
    }

    private void onDeleted(InstallationEvent event) {
        repoIndexingJobTriggerRepository.deleteAllByInstallationId(event.getInstallationId());
    }
}
