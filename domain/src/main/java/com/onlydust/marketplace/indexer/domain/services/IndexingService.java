package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IndexingService {
    private final RawStorageReader rawStorageReader;
    private final RawStorageRepository rawStorageRepository;

    public void indexUserById(Integer userId) {
        rawStorageReader.userById(userId).ifPresent(rawStorageRepository::save);
    }
}
