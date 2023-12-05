package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class UserExposerIndexer implements UserIndexer {
    UserIndexer indexer;
    Exposer<CleanAccount> exposer;

    @Override
    public Optional<CleanAccount> indexUser(Long userId) {
        final var user = indexer.indexUser(userId);
        user.ifPresent(exposer::expose);
        return user;
    }
}
