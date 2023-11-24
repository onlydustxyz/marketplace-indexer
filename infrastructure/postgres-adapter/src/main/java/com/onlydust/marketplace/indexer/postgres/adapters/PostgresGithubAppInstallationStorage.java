package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.GithubAppInstallationStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAppInstallationEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAppInstallationEntityRepository;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class PostgresGithubAppInstallationStorage implements GithubAppInstallationStorage {

    private final GithubAppInstallationEntityRepository githubAppInstallationEntityRepository;

    @Override
    public void save(GithubAppInstallation installation) {
        githubAppInstallationEntityRepository.save(GithubAppInstallationEntity.of(installation));
    }

    @Override
    public void addRepos(Long installationId, List<GithubRepo> repos) {
        final var installation = githubAppInstallationEntityRepository.findById(installationId)
                .orElseThrow(() -> OnlyDustException.notFound("Installation %d not found".formatted(installationId)));
        installation.getRepos().addAll(repos.stream().map(GithubRepoEntity::of).toList());
        githubAppInstallationEntityRepository.save(installation);
    }

    @Override
    public void delete(Long installationId) {
        githubAppInstallationEntityRepository.deleteById(installationId);
        githubAppInstallationEntityRepository.flush();
    }

    @Override
    public void removeRepos(Long installationId, List<Long> repoIds) {
        final var installation = githubAppInstallationEntityRepository.findById(installationId)
                .orElseThrow(() -> OnlyDustException.notFound("Installation %d not found".formatted(installationId)));
        installation.getRepos().removeIf(repo -> repoIds.contains(repo.getId()));
        githubAppInstallationEntityRepository.save(installation);
    }

    @Override
    public void setSuspendedAt(Long installationId, Date suspendedAt) {
        final var installation = githubAppInstallationEntityRepository.findById(installationId)
                .orElseThrow(() -> OnlyDustException.notFound("Installation %d not found".formatted(installationId)));
        githubAppInstallationEntityRepository.save(installation.toBuilder()
                .suspendedAt(suspendedAt)
                .build());
    }

    @Override
    public Optional<Long> findInstallationIdByAccount(Long accountId) {
        return githubAppInstallationEntityRepository.findByAccountId(accountId)
                .map(GithubAppInstallationEntity::getId);
    }
}
