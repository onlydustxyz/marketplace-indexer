package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.model.clean.User;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
public class IndexingService {
    private final RawStorageReader rawStorageReader;

    public User indexUserById(Integer userId) {
        final var user = rawStorageReader.userById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        final var socialAccounts = rawStorageReader.userSocialAccountsById(userId).orElseGet(ArrayList::new);
        return new User(user.getId(), user.getLogin(), socialAccounts);
    }
}
