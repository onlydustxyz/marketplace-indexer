package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;

import java.util.Optional;

public interface RepoIndexer {

    Optional<CleanRepo> indexRepo(Long repoId);

    Optional<CleanRepo> indexRepo(String repoOwner, String repoName);
}
