package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubAppInstallationRepository;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAppInstallationEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAppInstallationEntityRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostgresGithubAppInstallationRepository implements GithubAppInstallationRepository {

    private final GithubAppInstallationEntityRepository githubAppInstallationEntityRepository;

    @Override
    public void save(GithubAppInstallation installation) {
        githubAppInstallationEntityRepository.save(GithubAppInstallationEntity.of(installation));
    }

    @Override
    public void delete(Long installationId) {
        githubAppInstallationEntityRepository.deleteById(installationId);
    }
}
