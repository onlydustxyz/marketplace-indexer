package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;

import java.time.Instant;

public interface RepoStorage {
    void save(GithubRepo repo);

    void setPrivate(Long repoId);

    void setPublic(Long repoId);

    void setLastIndexedTime(Long repoId, Instant lastIndexedTime);

    void update(GithubRepo updated);
}
