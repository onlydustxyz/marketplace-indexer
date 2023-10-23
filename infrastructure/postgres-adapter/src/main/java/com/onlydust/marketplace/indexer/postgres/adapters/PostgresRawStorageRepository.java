package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageRepository;
import com.onlydust.marketplace.indexer.postgres.entities.raw.*;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@AllArgsConstructor
public class PostgresRawStorageRepository implements RawStorageRepository {
    final IssueRepository issueRepository;
    final UserRepository userRepository;
    final RepoRepository repoRepository;
    final PullRequestRepository pullRequestRepository;
    final RepoLanguagesRepository repoLanguagesRepository;
    final UserSocialAccountsRepository userSocialAccountsRepository;
    final PullRequestCommitsRepository pullRequestCommitsRepository;
    final PullRequestClosingIssueRepository pullRequestClosingIssueRepository;
    final PullRequestClosingIssueViewRepository pullRequestClosingIssueViewRepository;
    final PullRequestReviewsRepository pullRequestReviewsRepository;
    final RepoCheckRunsRepository repoCheckRunsRepository;

    @Override
    public Optional<RawRepo> repo(Long repoId) {
        return repoRepository.findById(repoId).map(Repo::getData);
    }

    @Override
    public Optional<RawRepo> repo(String repoOwner, String repoName) {
        return repoRepository.findByOwnerAndName(repoOwner, repoName).map(Repo::getData);
    }

    @Override
    public Stream<RawPullRequest> repoPullRequests(Long repoId) {
        return pullRequestRepository.findAllByRepoId(repoId).stream().map(PullRequest::getData);
    }

    @Override
    public Stream<RawIssue> repoIssues(Long repoId) {
        return issueRepository.findAllByRepoId(repoId).stream().map(Issue::getData);
    }

    @Override
    public RawLanguages repoLanguages(Long repoId) {
        return repoLanguagesRepository.findById(repoId).map(RepoLanguages::getData).orElse(new RawLanguages());
    }

    @Override
    public Optional<RawAccount> user(Long userId) {
        return userRepository.findById(userId).map(User::getData);
    }

    @Override
    public List<RawSocialAccount> userSocialAccounts(Long userId) {
        return userSocialAccountsRepository.findById(userId).map(UserSocialAccounts::getData).orElse(List.of());
    }

    @Override
    public Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber) {
        return pullRequestRepository.findByRepoIdAndNumber(repoId, prNumber).map(PullRequest::getData);
    }

    @Override
    public Optional<RawIssue> issue(Long repoId, Long issueNumber) {
        return issueRepository.findByRepoIdAndNumber(repoId, issueNumber).map(Issue::getData);
    }

    @Override
    public List<RawCodeReview> pullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return pullRequestReviewsRepository.findById(pullRequestId).map(PullRequestReview::getData).orElse(List.of());
    }

    @Override
    public List<RawCommit> pullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return pullRequestCommitsRepository.findById(pullRequestId).map(PullRequestCommits::getData).orElse(List.of());
    }

    @Override
    public Optional<RawCheckRuns> checkRuns(Long repoId, String sha) {
        return repoCheckRunsRepository.findById(new RepoCheckRuns.Id(repoId, sha)).map(RepoCheckRuns::getData);
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        final var closingIssues = pullRequestClosingIssueViewRepository.findAllByPullRequestRepoOwnerAndPullRequestRepoNameAndPullRequestNumber(repoOwner, repoName, pullRequestNumber).stream().toList();
        final var issues = closingIssues.stream().map(issue -> Pair.of(issue.getIssue().getId(), issue.getIssue().getNumber())).toList();
        return closingIssues.stream().findFirst().map(issue -> new RawPullRequestClosingIssues(issue.getPullRequest().getId(), issues));
    }

    @Override
    public void saveUser(RawAccount user) {
        userRepository.save(User.of(user));
    }

    @Override
    public void saveUserSocialAccounts(Long userId, List<RawSocialAccount> socialAccounts) {
        userSocialAccountsRepository.save(UserSocialAccounts.of(userId, socialAccounts));
    }

    @Override
    public void savePullRequest(Long repoId, RawPullRequest pullRequest) {
        pullRequestRepository.save(PullRequest.of(repoId, pullRequest));
    }

    @Override
    public void savePullRequestReviews(Long pullRequestId, List<RawCodeReview> codeReviews) {
        pullRequestReviewsRepository.save(PullRequestReview.of(pullRequestId, codeReviews));
    }

    @Override
    public void savePullRequestCommits(Long pullRequestId, List<RawCommit> commits) {
        pullRequestCommitsRepository.save(PullRequestCommits.of(pullRequestId, commits));
    }

    @Override
    public void saveCheckRuns(Long repoId, String sha, RawCheckRuns checkRuns) {
        repoCheckRunsRepository.save(RepoCheckRuns.of(repoId, sha, checkRuns));
    }

    @Override
    public void saveIssue(Long repoId, RawIssue issue) {
        issueRepository.save(Issue.of(repoId, issue));
    }

    @Override
    public void saveRepo(RawRepo repo) {
        repoRepository.save(Repo.of(repo));
    }

    @Override
    public void saveRepoLanguages(Long repoId, RawLanguages languages) {
        repoLanguagesRepository.save(RepoLanguages.of(repoId, languages));
    }

    @Override
    public void saveClosingIssues(RawPullRequestClosingIssues closingIssues) {
        pullRequestClosingIssueRepository.saveAll(closingIssues.issueIdNumbers().stream().map(issue -> PullRequestClosingIssue.of(closingIssues.pullRequestId(), issue.getLeft())).toList());
    }
}
