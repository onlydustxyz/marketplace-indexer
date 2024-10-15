package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import lombok.NonNull;

public interface UserFileExtensionStorage {

    void addModificationsForUserAndExtension(final @NonNull Long userId,
                                             final @NonNull String fileExtension,
                                             int commitCount,
                                             int fileCount,
                                             int modificationCount);

    void clear();
}
