package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import com.onlydust.marketplace.indexer.domain.model.clean.CodeReview;
import com.onlydust.marketplace.indexer.domain.model.clean.PullRequest;
import com.onlydust.marketplace.indexer.domain.model.clean.User;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class IndexingService {
    private final RawStorageReader rawStorageReader;

    public User indexUser(Integer userId) {
        final var user = rawStorageReader.user(userId).orElseThrow(() -> new NotFound("User not found"));
        final var socialAccounts = rawStorageReader.userSocialAccounts(userId);
        return new User(user.getId(), user.getLogin(), socialAccounts);
    }

    private List<CodeReview> indexPullRequestReview(Integer pullRequestId) {
        final var codeReviews = rawStorageReader.pullRequestReviews(pullRequestId);
        return codeReviews.stream().map(review -> {
            final var author = indexUser(review.getAuthor().getId());
            return new CodeReview(review.getId(), author);
        }).toList();
    }

    public PullRequest indexPullRequest(String repoOwner, String repoName, Integer prNumber) {
        final var pullRequest = rawStorageReader.pullRequest(repoOwner, repoName, prNumber).orElseThrow(() -> new NotFound("Pull request not found"));
        final var author = indexUser(pullRequest.getAuthor().getId());
        final var codeReviews = indexPullRequestReview(pullRequest.getId());
        final var requestedReviewers = pullRequest.getRequestedReviewers().stream().map(reviewer -> indexUser(reviewer.getId())).toList();
        return new PullRequest(pullRequest.getId(), author, codeReviews, requestedReviewers);
    }
}
