package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import com.onlydust.marketplace.indexer.domain.model.clean.PullRequest;
import com.onlydust.marketplace.indexer.domain.model.clean.User;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
public class IndexingService {
    private final RawStorageReader rawStorageReader;

    public User indexUser(Integer userId) {
        final var user = rawStorageReader.user(userId).orElseThrow(() -> new NotFound("User not found"));
        final var socialAccounts = rawStorageReader.userSocialAccounts(userId).orElseGet(ArrayList::new);
        return new User(user.getId(), user.getLogin(), socialAccounts);
    }

    public PullRequest indexPullRequest(String repoOwner, String repoName, Integer prNumber) {
        final var pullRequest = rawStorageReader.pullRequest(repoOwner, repoName, prNumber).orElseThrow(() -> new NotFound("Pull request not found"));
        final var author = indexUser(pullRequest.getAuthor().getId());
        return new PullRequest(pullRequest.getId(), author);
    }
}
