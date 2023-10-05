package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.User;

public interface RawStorageRepository extends RawStorageReader {
    void save(User user);
}
