package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.model.raw.RawUser;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CacheWriteRawStorageReaderDecorator implements RawStorageReader {
    private final RawStorageReader fetcher;
    private final RawStorageRepository cache;

    @Override
    public Optional<RawUser> user(Integer userId) {
        final var user = fetcher.user(userId);
        user.ifPresent(cache::save);
        return user;
    }

    @Override
    public Optional<List<RawSocialAccount>> userSocialAccounts(Integer userId) {
        final var socialAccounts = fetcher.userSocialAccounts(userId);
        socialAccounts.ifPresent(accounts -> cache.save(userId, accounts));
        return socialAccounts;
    }

    @Override
    public Optional<RawPullRequest> pullRequest(String repoOwner, String repoName, Integer prNumber) {
        final var pullRequest = fetcher.pullRequest(repoOwner, repoName, prNumber);
        pullRequest.ifPresent(cache::save);
        return pullRequest;
    }
}
