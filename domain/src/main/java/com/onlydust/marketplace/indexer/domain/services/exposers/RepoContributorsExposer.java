package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoContributorsStorage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RepoContributorsExposer implements Exposer<CleanRepo> {
    RepoContributorsStorage repoContributorsStorage;

    @Override
    public void expose(CleanRepo repo) {
        repoContributorsStorage.updateRepoContributors(repo.getId());
    }
}
