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
import java.util.Set;

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

        installation.repos().addAll(
                repos.stream()
                        .filter(repo -> installation.repos().stream().noneMatch(existing -> existing.getId().equals(repo.getId())))
                        .map(GithubRepoEntity::of).toList()
        );

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
        installation.repos().removeIf(repo -> repoIds.contains(repo.getId()));
        githubAppInstallationEntityRepository.save(installation);
    }

    @Override
    public void setSuspendedAt(Long installationId, Date suspendedAt) {
        final var installation = githubAppInstallationEntityRepository.findById(installationId)
                .orElseThrow(() -> OnlyDustException.notFound("Installation %d not found".formatted(installationId)));
        githubAppInstallationEntityRepository.save(installation.suspendedAt(suspendedAt));
    }

    @Override
    public Optional<Long> findInstallationIdByAccount(Long accountId) {
        return githubAppInstallationEntityRepository.findByAccountId(accountId)
                .map(GithubAppInstallationEntity::id);
    }

    @Override
    public void setPermissions(Long installationId, Set<String> permissions) {
        final var installation = githubAppInstallationEntityRepository.findById(installationId)
                .orElseThrow(() -> OnlyDustException.notFound("Installation %d not found".formatted(installationId)));
        githubAppInstallationEntityRepository.save(installation.permissions(permissions));
    }
}
