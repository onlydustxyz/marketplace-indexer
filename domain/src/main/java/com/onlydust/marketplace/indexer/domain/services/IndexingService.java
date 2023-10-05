package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.model.clean.User;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageRepository;
import lombok.AllArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
public class IndexingService {
    private final RawStorageReader rawStorageReader;
    private final RawStorageRepository rawStorageRepository;

    public User indexUserById(Integer userId) {
        final var user = rawStorageReader.userById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        rawStorageRepository.save(user);
        final var socialAccounts = rawStorageReader.userSocialAccountsById(userId).map(accounts -> {
            rawStorageRepository.save(userId, accounts);
            return accounts;
        }).orElseGet(ArrayList::new);

        return new User(user, socialAccounts);
    }
}
