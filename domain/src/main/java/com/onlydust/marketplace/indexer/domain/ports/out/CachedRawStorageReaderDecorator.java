package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.model.raw.RawUser;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CachedRawStorageReaderDecorator implements RawStorageReader {
    private final RawStorageReader rawStorageReader;
    private final RawStorageRepository rawStorageRepository;

    @Override
    public Optional<RawUser> userById(Integer userId) {
        final var user = rawStorageReader.userById(userId);
        user.ifPresent(rawStorageRepository::save);
        return user;
    }

    @Override
    public Optional<List<RawSocialAccount>> userSocialAccountsById(Integer userId) {
        final var socialAccounts = rawStorageReader.userSocialAccountsById(userId);
        socialAccounts.ifPresent(accounts -> rawStorageRepository.save(userId, accounts));
        return socialAccounts;
    }
}
