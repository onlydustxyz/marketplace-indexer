package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.exposition.UserFileExtensionStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubUserFileExtensionEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubUserFileExtensionsRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class PostgresUserFileExtensionsStorage implements UserFileExtensionStorage {
    private final GithubUserFileExtensionsRepository githubUserFileExtensionsRepository;

    @Override
    @Transactional
    public void addModificationsForUserAndExtension(final @NonNull Long userId,
                                                    final @NonNull String fileExtension,
                                                    int commitCount,
                                                    int fileCount,
                                                    int modificationCount) {
        githubUserFileExtensionsRepository.findById(new GithubUserFileExtensionEntity.PrimaryKey(userId, fileExtension))
                .ifPresentOrElse(entity -> entity.add(commitCount, fileCount, modificationCount),
                        () -> githubUserFileExtensionsRepository.persist(new GithubUserFileExtensionEntity(userId, fileExtension, commitCount, fileCount,
                                modificationCount)));
    }
}
