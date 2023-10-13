package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.models.clean.Repo;

public interface RepoIndexer {
    Repo indexRepo(Long repoId);
}
