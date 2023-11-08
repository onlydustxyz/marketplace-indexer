package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;

import java.util.List;

public interface GithubRepoStorage {
    void saveAll(List<GithubRepo> repos);
}
