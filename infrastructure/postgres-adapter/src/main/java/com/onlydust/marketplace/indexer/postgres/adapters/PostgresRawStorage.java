package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import com.onlydust.marketplace.indexer.postgres.entities.raw.*;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.notFound;

@AllArgsConstructor
public class PostgresRawStorage implements RawStorageWriter, RawStorageReader {
    final IssueRepository issueRepository;
    final UserRepository userRepository;
    final RepoRepository repoRepository;
    final PullRequestRepository pullRequestRepository;
    final RepoLanguagesRepository repoLanguagesRepository;
    final UserSocialAccountsRepository userSocialAccountsRepository;
    final PullRequestClosingIssueRepository pullRequestClosingIssueRepository;
    final PullRequestClosingIssueViewRepository pullRequestClosingIssueViewRepository;
    final PullRequestReviewsRepository pullRequestReviewsRepository;
    final CommitRepository commitRepository;

    @Override
    public Optional<RawRepo> repo(Long repoId) {
        return repoRepository.findById(repoId).map(RawRepoEntity::data);
    }

    @Override
    public Optional<RawRepo> repo(String repoOwner, String repoName) {
        return repoRepository.findByOwnerAndNameAndDeleted(repoOwner, repoName, false).map(RawRepoEntity::data);
    }

    @Override
    public Stream<RawPullRequest> repoPullRequests(Long repoId) {
        return pullRequestRepository.findAllByRepoId(repoId).stream().map(RawPullRequestEntity::getData);
    }

    @Override
    public Stream<RawIssue> repoIssues(Long repoId) {
        return issueRepository.findAllByRepoId(repoId).stream().map(RawIssueEntity::getData);
    }

    @Override
    public Optional<RawLanguages> repoLanguages(Long repoId) {
        return repoLanguagesRepository.findById(repoId).map(RawRepoLanguagesEntity::getData);
    }

    @Override
    public Optional<RawAccount> user(Long userId) {
        return userRepository.findById(userId).map(RawUserEntity::getData);
    }

    @Override
    public Optional<List<RawSocialAccount>> userSocialAccounts(Long userId) {
        return userSocialAccountsRepository.findById(userId).map(RawUserSocialAccountsEntity::getData);
    }

    @Override
    public Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber) {
        return pullRequestRepository.findByRepoIdAndNumber(repoId, prNumber).map(RawPullRequestEntity::getData);
    }

    @Override
    public Optional<RawIssue> issue(Long repoId, Long issueNumber) {
        return issueRepository.findByRepoIdAndNumber(repoId, issueNumber).map(RawIssueEntity::getData);
    }

    @Override
    public Optional<List<RawCodeReview>> pullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return pullRequestReviewsRepository.findById(pullRequestId).map(RawPullRequestReviewEntity::getData);
    }

    @Override
    public Optional<List<RawCommit>> pullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return pullRequestRepository.findById(pullRequestId)
                .flatMap(pr -> Optional.ofNullable(pr.getCommits()))
                .map(c -> c.stream().map(RawCommitEntity::getData).toList());
    }

    @Override
    public Optional<RawCommit> commit(Long repoId, String sha) {
        return commitRepository.findById(sha).flatMap(c -> Optional.ofNullable(c.getData()));
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        return pullRequestClosingIssueViewRepository.findById(new RawPullRequestClosingIssuesEntity.PrimaryKey(repoOwner, repoName, pullRequestNumber))
                .map(RawPullRequestClosingIssuesEntity::getData);
    }

    @Override
    public void saveUser(RawAccount user) {
        userRepository.merge(RawUserEntity.of(user));
    }

    @Override
    public void saveUserSocialAccounts(Long userId, List<RawSocialAccount> socialAccounts) {
        userSocialAccountsRepository.merge(RawUserSocialAccountsEntity.of(userId, socialAccounts));
    }

    @Override
    public void savePullRequest(RawPullRequest pullRequest) {
        pullRequestRepository.findById(pullRequest.getId()).ifPresentOrElse(
                pr -> pr.setData(pullRequest),
                () -> pullRequestRepository.merge(RawPullRequestEntity.of(pullRequest)));
    }

    @Override
    public void savePullRequestReviews(Long pullRequestId, List<RawCodeReview> codeReviews) {
        pullRequestReviewsRepository.merge(RawPullRequestReviewEntity.of(pullRequestId, codeReviews));
    }

    @Override
    @Transactional
    public void savePullRequestCommits(Long pullRequestId, List<RawCommit> commits) {
        pullRequestRepository.merge(pullRequestRepository.findById(pullRequestId)
                .orElseThrow(() -> notFound("Pull request not found: " + pullRequestId))
                .withCommits(commits));
    }

    @Override
    public void saveIssue(Long repoId, RawIssue issue) {
        issueRepository.merge(RawIssueEntity.of(repoId, issue));
    }

    @Override
    public void saveRepo(RawRepo repo) {
        repoRepository.merge(RawRepoEntity.of(repo));
    }

    @Override
    @Transactional
    public void deleteRepo(Long repoId) {
        repoRepository.findById(repoId).ifPresent(r -> r.deleted(true));
    }

    @Override
    public void saveRepoLanguages(Long repoId, RawLanguages languages) {
        repoLanguagesRepository.merge(RawRepoLanguagesEntity.of(repoId, languages));
    }

    @Override
    public void saveClosingIssues(String repoOwner, String repoName, Long pullRequestNumber, RawPullRequestClosingIssues closingIssues) {
        pullRequestClosingIssueRepository.merge(RawPullRequestClosingIssuesEntity.of(repoOwner, repoName, pullRequestNumber, closingIssues));
    }

    @Override
    public void deleteIssue(Long id) {
        issueRepository.deleteById(id);
    }

    @Override
    public void saveCommit(@NonNull Long repoId, @NonNull RawCommit commit) {
        commitRepository.merge(RawCommitEntity.of(repoId, commit));
    }

    @Override
    public void saveCommits(@NonNull Long repoId, @NonNull List<RawShortCommit> commits) {
        commitRepository.mergeAll(commits.stream().map(c -> RawCommitEntity.of(repoId, c)).toList());
    }
}
