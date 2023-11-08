package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.*;
import com.onlydust.marketplace.indexer.domain.models.raw.RawCheckRuns;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestClosingIssues;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;

@AllArgsConstructor
@Slf4j
public class PullRequestIndexingService implements PullRequestIndexer {
    private final RawStorageReader rawStorageReader;
    private final UserIndexer userIndexer;
    private final RepoIndexer repoIndexer;
    private final IssueIndexer issueIndexer;


    private List<CleanCodeReview> indexPullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        LOGGER.info("Indexing pull request reviews for repo {} and pull request {}", repoId, pullRequestId);
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
        LOGGER.info("Indexing pull request commits for repo {} and pull request {}", repoId, pullRequestNumber);
        final var commits = rawStorageReader.pullRequestCommits(repoId, pullRequestId, pullRequestNumber)
                .orElseGet(() -> {
                    LOGGER.warn("Unable to fetch pull request commits");
                    return List.of();
                });
        return commits.stream().map(commit -> Optional.ofNullable(commit.getAuthor())
                .or(() -> Optional.ofNullable(commit.getCommitter()))
                .filter(user -> user.getId() != null)
                .flatMap(user -> userIndexer.indexUser(user.getId()))
                .map(user -> CleanCommit.of(commit, user))
                .orElseGet(() -> {
                    LOGGER.warn("Unable to index commit {} for pull request {}/{}", commit.getSha(), repoId, pullRequestNumber);
                    return null;
                })).filter(Objects::nonNull).toList();
    }

    private List<CleanCheckRun> indexCheckRuns(Long repoId, String sha) {
        LOGGER.info("Indexing check runs for repo {} and sha {}", repoId, sha);
        final var checkRuns = rawStorageReader.checkRuns(repoId, sha).map(RawCheckRuns::getCheckRuns).orElse(List.of());
        return checkRuns.stream().map(CleanCheckRun::of).toList();
    }

    private List<CleanIssue> indexClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        LOGGER.info("Indexing closing issues for repo {} and pull request {}", repoOwner, pullRequestNumber);
        final var closingIssues = rawStorageReader.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber)
                .orElseGet(() -> {
                    LOGGER.warn("Unable to fetch pull request {}/{}/{} closing issues", repoOwner, repoName, pullRequestNumber);
                    return new RawPullRequestClosingIssues();
                });

        return closingIssues.issues().stream()
                .map(reference -> issueIndexer.indexIssue(reference.repoOwner(), reference.repoName(), reference.number()).orElseGet(() -> {
                    LOGGER.warn("Unable to index issue {}", reference);
                    return null;
                }))
                .filter(Objects::nonNull).toList();
    }

    @Override
    public Optional<CleanPullRequest> indexPullRequest(String repoOwner, String repoName, Long prNumber) {
        LOGGER.info("Indexing pull request {} for repo {}/{}", prNumber, repoOwner, repoName);
        return repoIndexer.indexRepo(repoOwner, repoName).flatMap(repo -> {
            final var pullRequest = rawStorageReader.pullRequest(repo.getId(), prNumber).orElseThrow(() -> OnlyDustException.notFound("Pull request not found"));

            return userIndexer.indexUser(pullRequest.getAuthor().getId()).map(author -> {
                final var codeReviews = indexPullRequestReviews(repo.getId(), pullRequest.getId(), prNumber);
                final var requestedReviewers = pullRequest.getRequestedReviewers().stream().map(reviewer -> userIndexer.indexUser(reviewer.getId()).orElseGet(() -> {
                    LOGGER.warn("User {} not found, skipping requested reviewer {}", reviewer.getId(), reviewer.getLogin());
                    return null;
                })).filter(Objects::nonNull).toList();
                final var commits = indexPullRequestCommits(repo.getId(), pullRequest.getId(), prNumber);
                final List<CleanCheckRun> checkRuns = isNull(pullRequest.getHead().getRepo()) ? List.of() : indexCheckRuns(pullRequest.getHead().getRepo().getId(), pullRequest.getHead().getSha());
                final var closingIssues = indexClosingIssues(pullRequest.getBase().getRepo().getOwner().getLogin(), pullRequest.getBase().getRepo().getName(), pullRequest.getNumber());
                return CleanPullRequest.of(
                        pullRequest,
                        repo,
                        author,
                        codeReviews,
                        requestedReviewers,
                        commits,
                        checkRuns,
                        closingIssues
                );
            });
        });
    }

}
