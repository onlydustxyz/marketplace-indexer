package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageWriter;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import com.onlydust.marketplace.indexer.postgres.entities.raw.*;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.*;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.notFound;

@AllArgsConstructor
public class PostgresRawStorage implements RawStorageWriter, RawStorageReader, PublicEventRawStorageReader, PublicEventRawStorageWriter {
    final IssueRepository issueRepository;
    final UserRepository userRepository;
    final RepoRepository repoRepository;
    final PullRequestRepository pullRequestRepository;
    final RepoLanguagesRepository repoLanguagesRepository;
    final UserSocialAccountsRepository userSocialAccountsRepository;
    final PullRequestClosingIssueRepository pullRequestClosingIssueRepository;
    final PullRequestClosingIssueViewRepository pullRequestClosingIssueViewRepository;
    final PullRequestReviewsRepository pullRequestReviewsRepository;
    final PublicEventRepository publicEventRepository;
    final CommitRepository commitRepository;

    @Override
    public Optional<RawRepo> repo(Long repoId) {
        return repoRepository.findById(repoId).map(RawRepoEntity::getData);
    }

    @Override
    public Optional<RawRepo> repo(String repoOwner, String repoName) {
        return repoRepository.findByOwnerAndNameAndDeleted(repoOwner, repoName, false).map(RawRepoEntity::getData);
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
        return commitRepository.findById(sha).map(RawCommitEntity::getData);
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        return pullRequestClosingIssueViewRepository.findById(new RawPullRequestClosingIssuesEntity.Id(repoOwner, repoName, pullRequestNumber))
                .map(RawPullRequestClosingIssuesEntity::getData);
    }

    @Override
    public Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since) {
        return publicEventRepository.findAllByActorIdAndCreatedAtGreaterThanEqual(userId, since).stream().map(RawPublicEventEntity::event);
    }

    @Override
    public void saveUser(RawAccount user) {
        userRepository.save(RawUserEntity.of(user));
    }

    @Override
    public void saveUserSocialAccounts(Long userId, List<RawSocialAccount> socialAccounts) {
        userSocialAccountsRepository.save(RawUserSocialAccountsEntity.of(userId, socialAccounts));
    }

    @Override
    public void savePullRequest(RawPullRequest pullRequest) {
        pullRequestRepository.save(RawPullRequestEntity.of(pullRequest));
    }

    @Override
    public void savePullRequestReviews(Long pullRequestId, List<RawCodeReview> codeReviews) {
        pullRequestReviewsRepository.save(RawPullRequestReviewEntity.of(pullRequestId, codeReviews));
    }

    @Override
    public void savePullRequestCommits(Long pullRequestId, List<RawCommit> commits) {
        final var pullRequest = pullRequestRepository.findById(pullRequestId)
                .orElseThrow(() -> notFound("Pull request not found: " + pullRequestId))
                .withCommits(commits);

        pullRequestRepository.save(pullRequest);
    }

    @Override
    public void saveIssue(Long repoId, RawIssue issue) {
        issueRepository.save(RawIssueEntity.of(repoId, issue));
    }

    @Override
    public void saveRepo(RawRepo repo) {
        repoRepository.save(RawRepoEntity.of(repo));
    }

    @Override
    public void deleteRepo(Long repoId) {
        repoRepository.findById(repoId).ifPresent(r -> repoRepository.save(r.toBuilder().deleted(true).build()));
    }

    @Override
    public void saveRepoLanguages(Long repoId, RawLanguages languages) {
        repoLanguagesRepository.save(RawRepoLanguagesEntity.of(repoId, languages));
    }

    @Override
    public void saveClosingIssues(String repoOwner, String repoName, Long pullRequestNumber, RawPullRequestClosingIssues closingIssues) {
        pullRequestClosingIssueRepository.save(RawPullRequestClosingIssuesEntity.of(repoOwner, repoName, pullRequestNumber, closingIssues));
    }

    @Override
    public void deleteIssue(Long id) {
        issueRepository.deleteById(id);
    }

    @Override
    public void saveCommit(Long repoId, RawCommit commit) {
        commitRepository.save(RawCommitEntity.of(repoId, commit));
    }

    @Override
    public void savePublicEvent(RawPublicEvent rawEvent) {
        publicEventRepository.save(RawPublicEventEntity.of(rawEvent));
    }
}
