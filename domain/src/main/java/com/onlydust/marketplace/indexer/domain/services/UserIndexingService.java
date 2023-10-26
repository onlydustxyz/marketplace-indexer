package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class UserIndexingService implements UserIndexer {
    private final RawStorageReader rawStorageReader;

    @Override
    public Optional<CleanAccount> indexUser(Long userId) {
        LOGGER.info("Indexing user {}", userId);
        return rawStorageReader.user(userId).map(user -> {
            final var socialAccounts = rawStorageReader.userSocialAccounts(userId);
            return CleanAccount.of(user, socialAccounts);
        });
    }
}
