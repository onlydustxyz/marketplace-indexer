package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.entities.OldRepoIndexesEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.OldRepoIndexesEntityRepository;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@AllArgsConstructor
public class PostgresOldRepoIndexingJobStorage implements RepoIndexingJobStorage {
    private final OldRepoIndexesEntityRepository oldRepoIndexesEntityRepository;

    @Override
    public Set<Long> installationIds() {
        return Set.of();
    }

    @Override
    public Set<RepoIndexingJobTrigger> reposUpdatedBefore(Long installationId, Instant since) {
        return Set.of();
    }

    @Override
    public void deleteInstallation(Long installationId) {
    }

    @Override
    public void deleteInstallationForRepos(Long installationId, List<Long> repoIds) {
    }

    @Override
    public void setSuspendedAt(Long installationId, Date suspendedAt) {
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

    @Override
    public void configureRepoForFullIndexing(Long repoId, Boolean isPublic) {
        if (isPublic)
            oldRepoIndexesEntityRepository.save(new OldRepoIndexesEntity(repoId));
    }

    @Override
    public void setInstallationForRepos(Long installationId, RepoIndexingJobTrigger... triggers) {
        oldRepoIndexesEntityRepository.saveAll(Stream.of(triggers)
                .map(trigger -> new OldRepoIndexesEntity(trigger.getRepoId()))
                .toList());
    }

    @Override
    public void setPrivate(Long repoId) {
        oldRepoIndexesEntityRepository.deleteById(repoId);
    }

    @Override
    public void setPublic(Long repoId) {
        oldRepoIndexesEntityRepository.save(new OldRepoIndexesEntity(repoId));
    }

    @Override
    public void configureRepoForLightIndexing(Long repoId) {
        oldRepoIndexesEntityRepository.deleteById(repoId);
    }
}
