package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class UserIndexingService implements UserIndexer {
    private final RawStorageReader rawStorageReader;

    @Override
    public CleanAccount indexUser(Long userId) {
        LOGGER.info("Indexing user {}", userId);
        final var user = rawStorageReader.user(userId).orElseThrow(() -> OnlyDustException.notFound("User not found"));
        final var socialAccounts = rawStorageReader.userSocialAccounts(userId);
        return CleanAccount.of(user, socialAccounts);
    }
}
