package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import com.onlydust.marketplace.indexer.domain.mappers.UserMapper;
import com.onlydust.marketplace.indexer.domain.models.clean.User;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class UserIndexingService implements UserIndexer {
    private final RawStorageReader rawStorageReader;

    @Override
    public User indexUser(Long userId) {
        LOGGER.info("Indexing user {}", userId);
        final var user = rawStorageReader.user(userId).orElseThrow(() -> new NotFound("User not found"));
        final var socialAccounts = rawStorageReader.userSocialAccounts(userId);
        return UserMapper.map(user, socialAccounts);
    }
}
