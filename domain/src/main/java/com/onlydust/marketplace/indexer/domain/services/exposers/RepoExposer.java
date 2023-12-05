package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoStorage;
import lombok.AllArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
public class RepoExposer implements Exposer<CleanRepo> {
    RepoStorage repoStorage;

    @Override
    public void expose(CleanRepo repo) {
        repoStorage.save(GithubRepo.of(repo));
        repoStorage.setLastIndexedTime(repo.getId(), Instant.now());
    }
}
