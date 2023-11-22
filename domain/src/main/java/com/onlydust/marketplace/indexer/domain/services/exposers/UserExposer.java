package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.AccountStorage;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class UserExposer implements UserIndexer {
    UserIndexer indexer;
    AccountStorage accountStorage;

    private void expose(CleanAccount user) {
        accountStorage.save(GithubAccount.of(user));
    }

    @Override
    public Optional<CleanAccount> indexUser(Long userId) {
        final var user = indexer.indexUser(userId);
        user.ifPresent(this::expose);
        return user;
    }
}
