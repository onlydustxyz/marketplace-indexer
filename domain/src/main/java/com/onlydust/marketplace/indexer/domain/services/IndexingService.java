package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import com.onlydust.marketplace.indexer.domain.mappers.RepoMapper;
import com.onlydust.marketplace.indexer.domain.mappers.UserMapper;
import com.onlydust.marketplace.indexer.domain.models.clean.*;
import com.onlydust.marketplace.indexer.domain.models.raw.RawCheckRuns;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestClosingIssues;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Slf4j
public class IndexingService {
    private final RawStorageReader rawStorageReader;

    public Repo indexRepo(Long repoId) {
        final var repo = rawStorageReader.repo(repoId).orElseThrow(() -> new NotFound("Repo not found"));
        final var pullRequests = rawStorageReader.repoPullRequests(repoId).stream().map(pr -> indexPullRequest(repo.getOwner().getLogin(), repo.getName(), pr.getNumber())).toList();
        final var issues = rawStorageReader.repoIssues(repoId).stream().map(issue -> indexIssue(repo.getOwner().getLogin(), repo.getName(), issue.getNumber())).toList();
        final var languages = rawStorageReader.repoLanguages(repoId);
        return RepoMapper.map(repo, pullRequests, issues, languages);
    }

    public User indexUser(Long userId) {
        final var user = rawStorageReader.user(userId).orElseThrow(() -> new NotFound("User not found"));
        final var socialAccounts = rawStorageReader.userSocialAccounts(userId);
        return UserMapper.map(user, socialAccounts);
    }

    private List<CodeReview> indexPullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        final var codeReviews = rawStorageReader.pullRequestReviews(repoId, pullRequestId, pullRequestNumber);
        return codeReviews.stream().map(review -> {
            final var author = indexUser(review.getAuthor().getId());
            return new CodeReview(review.getId(), author);
        }).toList();
    }

    private List<Commit> indexPullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        final var commits = rawStorageReader.pullRequestCommits(repoId, pullRequestId, pullRequestNumber);
        return commits.stream().map(commit -> {
            final var author = Objects.isNull(commit.getAuthor()) ? commit.getCommitter() : commit.getAuthor();
            return Objects.isNull(author) ? null : new Commit(commit.getSha(), indexUser(author.getId()));
        }).filter(commit -> !Objects.isNull(commit)).toList();
    }

    private List<CheckRun> indexCheckRuns(Long repoId, String sha) {
        final var checkRuns = rawStorageReader.checkRuns(repoId, sha).map(RawCheckRuns::getCheckRuns).orElse(new ArrayList<>());
        return checkRuns.stream().map(checkRun -> {
            return new CheckRun(checkRun.getId());
        }).toList();
    }

    private List<Issue> indexClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        final var closingIssues = rawStorageReader.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber);
        return closingIssues.map(RawPullRequestClosingIssues::issueIdNumbers).orElse(new ArrayList<>()).stream().map(issue -> indexIssue(repoOwner, repoName, issue.getRight())).toList();
    }

    public PullRequest indexPullRequest(String repoOwner, String repoName, Long prNumber) {
        final var repo = rawStorageReader.repo(repoOwner, repoName).orElseThrow(() -> new NotFound("Repo not found"));
        final var pullRequest = rawStorageReader.pullRequest(repo.getId(), prNumber).orElseThrow(() -> new NotFound("Pull request not found"));
        final var author = indexUser(pullRequest.getAuthor().getId());
        final var codeReviews = indexPullRequestReviews(repo.getId(), pullRequest.getId(), prNumber);
        final var requestedReviewers = pullRequest.getRequestedReviewers().stream().map(reviewer -> indexUser(reviewer.getId())).toList();
        final var commits = indexPullRequestCommits(repo.getId(), pullRequest.getId(), prNumber);
        final var checkRuns = Objects.isNull(pullRequest.getHead().getRepo()) ? new ArrayList<CheckRun>() : indexCheckRuns(pullRequest.getHead().getRepo().getId(), pullRequest.getHead().getSha());
        final var closingIssues = indexClosingIssues(pullRequest.getBase().getRepo().getOwner().getLogin(), pullRequest.getBase().getRepo().getName(), pullRequest.getNumber());
        return new PullRequest(pullRequest.getId(), author, codeReviews, requestedReviewers, commits, checkRuns, closingIssues);
    }


    public Issue indexIssue(String repoOwner, String repoName, Long issueNumber) {
        final var repo = rawStorageReader.repo(repoOwner, repoName).orElseThrow(() -> new NotFound("Repo not found"));
        final var issue = rawStorageReader.issue(repo.getId(), issueNumber).orElseThrow(() -> new NotFound("Issue not found"));
        final var assignees = issue.getAssignees().stream().map(assignee -> indexUser(assignee.getId())).toList();
        return new Issue(issue.getId(), assignees);
    }
}
