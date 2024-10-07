package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import java.util.Set;

public interface UserFileExtensionStorage {

    void addCommit(Long userId, Set<String> fileExtensions);
}
