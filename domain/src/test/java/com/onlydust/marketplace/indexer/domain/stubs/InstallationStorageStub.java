package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.GithubAppInstallationStorage;

import java.util.ArrayList;
import java.util.List;

public class InstallationStorageStub implements GithubAppInstallationStorage {
    private final List<GithubAppInstallation> installations = new ArrayList<>();

    @Override
    public void save(GithubAppInstallation installation) {
        this.installations.add(installation);
    }

    @Override
    public void delete(Long installationId) {
        this.installations.removeIf(installation -> installation.getId().equals(installationId));
    }

    public List<GithubAppInstallation> installations() {
        return installations;
    }
}
