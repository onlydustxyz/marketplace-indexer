package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.GithubAppInstallationStorage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class InstallationStorageStub implements GithubAppInstallationStorage {
    private final List<GithubAppInstallation> installations = new ArrayList<>();

    @Override
    public void save(GithubAppInstallation installation) {
        this.installations.add(installation);
    }

    @Override
    public void addRepos(Long installationId, List<GithubRepo> repos) {
        final var installation = this.installations.stream().filter(installation1 -> installation1.getId().equals(installationId)).findFirst().orElseThrow();
        installation.getRepos().addAll(repos);
    }

    @Override
    public void delete(Long installationId) {
        this.installations.removeIf(installation -> installation.getId().equals(installationId));
    }

    @Override
    public void removeRepos(Long installationId, List<Long> repoIds) {
        final var installation = this.installations.stream().filter(installation1 -> installation1.getId().equals(installationId)).findFirst().orElseThrow();
        installation.getRepos().removeIf(repo -> repoIds.contains(repo.getId()));
    }

    @Override
    public void setSuspendedAt(Long installationId, Instant suspendedAt) {
        final var installation = this.installations.stream().filter(installation1 -> installation1.getId().equals(installationId)).findFirst().orElseThrow();
        installation.setSuspendedAt(suspendedAt);
    }

    public List<GithubAppInstallation> installations() {
        return installations;
    }
}
