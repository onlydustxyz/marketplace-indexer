package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.raw.RawPullRequest;
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
    public Optional<RawUser> user(Integer userId) {
        final var user = rawStorageReader.user(userId);
        user.ifPresent(rawStorageRepository::save);
        return user;
    }

    @Override
    public Optional<List<RawSocialAccount>> userSocialAccounts(Integer userId) {
        final var socialAccounts = rawStorageReader.userSocialAccounts(userId);
        socialAccounts.ifPresent(accounts -> rawStorageRepository.save(userId, accounts));
        return socialAccounts;
    }

    @Override
    public Optional<RawPullRequest> pullRequest(String repoOwner, String repoName, Integer prNumber) {
        final var pullRequest = rawStorageReader.pullRequest(repoOwner, repoName, prNumber);
        pullRequest.ifPresent(rawStorageRepository::save);
        return pullRequest;
    }
}
