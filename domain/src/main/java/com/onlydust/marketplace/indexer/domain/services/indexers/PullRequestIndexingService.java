package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanCodeReview;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.*;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class PullRequestIndexingService implements PullRequestIndexer {
    private final RawStorageReader rawStorageReader;
    private final UserIndexer userIndexer;
    private final RepoIndexer repoIndexer;
    private final IssueIndexer issueIndexer;
    private final CommitIndexer commitIndexer;

    private List<CleanCodeReview> indexPullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        LOGGER.debug("Indexing pull request reviews for repo {} and pull request {}", repoId, pullRequestId);
        final var codeReviews = rawStorageReader.pullRequestReviews(repoId, pullRequestId, pullRequestNumber)
                .orElseGet(() -> {
                    LOGGER.warn("Unable to fetch pull request reviews");
                    return List.of();
                });
        return codeReviews.stream()
                .filter(review -> review.getAuthor() != null && review.getAuthor().getId() != null)
                .map(review ->
                        userIndexer.indexUser(review.getAuthor().getId())
                                .map(author -> CleanCodeReview.of(review, author))
                                .orElseGet(() -> {
                                    LOGGER.warn("User {} not found, skipping review {}", review.getAuthor().getId(), review.getId());
                                    return null;
                                })
                ).filter(Objects::nonNull).toList();
    }

    private List<CleanCommit> indexPullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        LOGGER.debug("Indexing pull request commits for repo {} and pull request {}", repoId, pullRequestNumber);
        return rawStorageReader.pullRequestCommits(repoId, pullRequestId, pullRequestNumber)
                .orElseGet(() -> {
                    LOGGER.warn("Unable to fetch pull request commits");
                    return List.of();
                }).stream()
                .flatMap(c -> commitIndexer.indexCommit(repoId, c.getSha()).stream())
                .toList();
    }

    private List<CleanIssue> indexClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        LOGGER.debug("Indexing closing issues for repo {} and pull request {}", repoOwner, pullRequestNumber);
        final var closingIssues = rawStorageReader.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber);

        if (closingIssues.isEmpty()) {
            LOGGER.warn("Unable to fetch pull request {}/{}/{} closing issues", repoOwner, repoName, pullRequestNumber);
            return List.of();
        }

        return closingIssues.get().issues().stream()
                .map(reference -> issueIndexer.indexIssue(reference.repoOwner(), reference.repoName(), reference.number()).orElseGet(() -> {
                    LOGGER.warn("Unable to index issue {}", reference);
                    return null;
                }))
                .filter(Objects::nonNull).toList();
    }

    @Override
    @Transactional
    public Optional<CleanPullRequest> indexPullRequest(String repoOwner, String repoName, Long prNumber) {
        LOGGER.debug("Indexing pull request {} for repo {}/{}", prNumber, repoOwner, repoName);
        return repoIndexer.indexRepo(repoOwner, repoName).flatMap(repo -> {
            final var pullRequest = rawStorageReader.pullRequest(repo.getId(), prNumber)
                    .orElseThrow(() -> OnlyDustException.notFound(("Pull request %d/%d not found").formatted(repo.getId(), prNumber)));

            return userIndexer.indexUser(pullRequest.getAuthor().getId()).map(author -> {
                final var codeReviews = indexPullRequestReviews(repo.getId(), pullRequest.getId(), prNumber);
                final var requestedReviewers =
                        pullRequest.getRequestedReviewers().stream().map(reviewer -> userIndexer.indexUser(reviewer.getId()).orElseGet(() -> {
                            LOGGER.warn("User {} not found, skipping requested reviewer {}", reviewer.getId(), reviewer.getLogin());
                            return null;
                        })).filter(Objects::nonNull).toList();
                final var commits = indexPullRequestCommits(repo.getId(), pullRequest.getId(), prNumber);
                final var closingIssues = indexClosingIssues(repoOwner, repoName, prNumber);
                return CleanPullRequest.of(
                        pullRequest,
                        repo,
                        author,
                        codeReviews,
                        requestedReviewers,
                        commits,
                        closingIssues
                );
            });
        });
    }

}
