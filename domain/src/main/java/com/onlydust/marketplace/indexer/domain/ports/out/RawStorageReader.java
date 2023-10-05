package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.User;

import java.util.Optional;

public interface RawStorageReader {
    Optional<User> userById(Integer id);
}
