package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.exposition.UserFileExtensionStorage;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubUserFileExtensionsRepository;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class PostgresUserFileExtensionsStorage implements UserFileExtensionStorage {
    private final GithubUserFileExtensionsRepository githubUserFileExtensionsRepository;

    @Override
    public void addCommit(Long userId, Set<String> fileExtensions) {
        githubUserFileExtensionsRepository.addCommitForUserAndExtensions(userId, fileExtensions.toArray(String[]::new));
    }
}
