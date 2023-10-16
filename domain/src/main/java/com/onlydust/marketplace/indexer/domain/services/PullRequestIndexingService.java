package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import com.onlydust.marketplace.indexer.domain.models.clean.*;
import com.onlydust.marketplace.indexer.domain.models.raw.RawCheckRuns;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestClosingIssues;
import com.onlydust.marketplace.indexer.domain.ports.in.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@AllArgsConstructor
@Slf4j
public class PullRequestIndexingService implements PullRequestIndexer {
    private final RawStorageReader rawStorageReader;
    private final UserIndexer userIndexer;
    private final IssueIndexer issueIndexer;


    private List<CodeReview> indexPullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        LOGGER.info("Indexing pull request reviews for repo {} and pull request {}", repoId, pullRequestId);
        final var codeReviews = rawStorageReader.pullRequestReviews(repoId, pullRequestId, pullRequestNumber);
        return codeReviews.stream().map(review -> {
            final var author = userIndexer.indexUser(review.getAuthor().getId());
            return new CodeReview(review.getId(), author);
        }).toList();
    }

    private List<Commit> indexPullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        LOGGER.info("Indexing pull request commits for repo {} and pull request {}", repoId, pullRequestId);
        final var commits = rawStorageReader.pullRequestCommits(repoId, pullRequestId, pullRequestNumber);
        return commits.stream().map(commit -> {
            final var author = isNull(commit.getAuthor()) ? commit.getCommitter() : commit.getAuthor();
            return isNull(author) ? null : new Commit(commit.getSha(), userIndexer.indexUser(author.getId()));
        }).filter(commit -> !isNull(commit)).toList();
    }

    private List<CheckRun> indexCheckRuns(Long repoId, String sha) {
        LOGGER.info("Indexing check runs for repo {} and sha {}", repoId, sha);
        final var checkRuns = rawStorageReader.checkRuns(repoId, sha).map(RawCheckRuns::getCheckRuns).orElse(List.of());
        return checkRuns.stream().map(checkRun -> {
            return new CheckRun(checkRun.getId());
        }).toList();
    }

    private List<Issue> indexClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        LOGGER.info("Indexing closing issues for repo {} and pull request {}", repoOwner, pullRequestNumber);
        final var closingIssues = rawStorageReader.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber);
        return closingIssues.map(RawPullRequestClosingIssues::issueIdNumbers).orElse(List.of())
                .stream().map(issue -> issueIndexer.indexIssue(repoOwner, repoName, issue.getRight())).toList();
    }

    @Override
    public PullRequest indexPullRequest(String repoOwner, String repoName, Long prNumber) {
        LOGGER.info("Indexing pull request {} for repo {}/{}", prNumber, repoOwner, repoName);
        final var repo = rawStorageReader.repo(repoOwner, repoName).orElseThrow(() -> new NotFound("Repo not found"));
        final var pullRequest = rawStorageReader.pullRequest(repo.getId(), prNumber).orElseThrow(() -> new NotFound("Pull request not found"));
        final var author = userIndexer.indexUser(pullRequest.getAuthor().getId());
        final var codeReviews = indexPullRequestReviews(repo.getId(), pullRequest.getId(), prNumber);
        final var requestedReviewers = pullRequest.getRequestedReviewers().stream().map(reviewer -> userIndexer.indexUser(reviewer.getId())).toList();
        final var commits = indexPullRequestCommits(repo.getId(), pullRequest.getId(), prNumber);
        final var checkRuns = isNull(pullRequest.getHead().getRepo()) ? new ArrayList<CheckRun>() : indexCheckRuns(pullRequest.getHead().getRepo().getId(), pullRequest.getHead().getSha());
        final var closingIssues = indexClosingIssues(pullRequest.getBase().getRepo().getOwner().getLogin(), pullRequest.getBase().getRepo().getName(), pullRequest.getNumber());
        return new PullRequest(pullRequest.getId(), author, codeReviews, requestedReviewers, commits, checkRuns, closingIssues);
    }

}
