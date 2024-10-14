package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.GithubAppInstallationStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAppInstallationEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAppInstallationEntityRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.notFound;

@AllArgsConstructor
public class PostgresGithubAppInstallationStorage implements GithubAppInstallationStorage {

    private final GithubAppInstallationEntityRepository githubAppInstallationEntityRepository;

    @Override
    public void save(GithubAppInstallation installation) {
        githubAppInstallationEntityRepository.merge(GithubAppInstallationEntity.of(installation));
    }

    @Override
    public void addRepos(Long installationId, List<GithubRepo> repos) {
        githubAppInstallationEntityRepository.merge(installation(installationId).withAddedRepos(repos));
    }

    @Override
    public void delete(Long installationId) {
        githubAppInstallationEntityRepository.deleteById(installationId);
        githubAppInstallationEntityRepository.flush();
    }

    @Override
    @Transactional
    public void removeRepos(Long installationId, List<Long> repoIds) {
        installation(installationId)
                .repos().removeIf(repo -> repoIds.contains(repo.getId()));
    }

    @Override
    @Transactional
    public void setSuspendedAt(Long installationId, Date suspendedAt) {
        installation(installationId).suspendedAt(suspendedAt);
    }

    @Override
    public Optional<Long> findInstallationIdByAccount(Long accountId) {
        return githubAppInstallationEntityRepository.findByAccountId(accountId)
                .map(GithubAppInstallationEntity::id);
    }

    @Override
    @Transactional
    public void setPermissions(Long installationId, Set<String> permissions) {
        installation(installationId).permissions(permissions);
    }

    private GithubAppInstallationEntity installation(Long installationId) {
        return githubAppInstallationEntityRepository.findById(installationId)
                .orElseThrow(() -> notFound("Installation %d not found".formatted(installationId)));
    }
}
