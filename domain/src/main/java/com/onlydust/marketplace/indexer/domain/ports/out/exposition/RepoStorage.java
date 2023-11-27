package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;

public interface RepoStorage {
    void save(GithubRepo repo);

    void setPrivate(Long repoId);

    void setPublic(Long repoId);
}
