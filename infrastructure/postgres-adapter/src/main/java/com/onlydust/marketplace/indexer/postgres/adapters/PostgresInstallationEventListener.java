package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.ports.out.EventListener;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccountEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAccountRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostgresInstallationEventListener implements EventListener<InstallationEvent> {
    private final GithubAccountRepository githubAccountRepository;
    private final GithubRepoRepository githubRepoRepository;

    @Override
    public void onEvent(InstallationEvent event) {
        switch (event.getAction()) {
            case CREATED -> onCreated(event);
            case DELETED -> onDeleted(event);
        }
    }

    private void onCreated(InstallationEvent event) {
        githubAccountRepository.save(GithubAccountEntity.of(GithubAccount.of(event)));
        githubRepoRepository.saveAll(event.getRepos().stream()
                .map(GithubRepo::of)
                .map(GithubRepoEntity::of)
                .toList());
    }

    private void onDeleted(InstallationEvent event) {
        githubAccountRepository.save(GithubAccountEntity.of(GithubAccount.of(event.getAccount())));
    }
}
