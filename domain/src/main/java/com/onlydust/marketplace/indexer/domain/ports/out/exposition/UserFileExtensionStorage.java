package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

public interface UserFileExtensionStorage {

    void addModificationsForUserAndExtension(Long userId, String fileExtension, int commitCount, int fileCount, int modificationCount);
}
