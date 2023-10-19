package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;

import java.util.List;

public interface GithubRepoRepository {
    void saveAll(List<GithubRepo> repos);

    void deleteAll(List<Long> repoIds);
}
