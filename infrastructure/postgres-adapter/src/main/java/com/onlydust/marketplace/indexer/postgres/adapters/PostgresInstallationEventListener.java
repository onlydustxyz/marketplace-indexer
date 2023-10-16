package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.EventListener;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAccountRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoRepository;
import lombok.AllArgsConstructor;

import javax.transaction.Transactional;

@AllArgsConstructor
@Transactional
public class PostgresInstallationEventListener implements EventListener<InstallationEvent> {
    private final GithubAccountRepository githubAccountRepository;
    private final GithubRepoRepository githubRepoRepository;

    @Override
    public void onEvent(InstallationEvent event) {
        githubAccountRepository.save(GithubAccount.of(event.getInstallationId(), event.getAccount()));
        githubRepoRepository.saveAll(event.getRepos().stream().map(repo -> GithubRepo.of(event.getAccount().id(), repo)).toList());
    }
}
