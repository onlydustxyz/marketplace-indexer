package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.exposition.UserFileExtensionStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubUserFileExtensionEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubUserFileExtensionsRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostgresUserFileExtensionsStorage implements UserFileExtensionStorage {
    private final GithubUserFileExtensionsRepository githubUserFileExtensionsRepository;

    @Override
    public void addModificationsForUserAndExtension(Long userId, String fileExtension, int commitCount, int fileCount, int modificationCount) {
        final var entity = githubUserFileExtensionsRepository.findByUserIdAndFileExtension(userId, fileExtension)
                .orElse(new GithubUserFileExtensionEntity(userId, fileExtension));
        entity.add(commitCount, fileCount, modificationCount);
        githubUserFileExtensionsRepository.save(entity);
    }
}
