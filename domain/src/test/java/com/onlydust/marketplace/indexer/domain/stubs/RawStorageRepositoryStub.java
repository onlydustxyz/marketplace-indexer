package com.onlydust.marketplace.indexer.domain.stubs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageRepository;
import org.assertj.core.groups.Tuple;

import java.io.IOException;
import java.util.*;

public class RawStorageRepositoryStub implements RawStorageRepository {
    final List<RawUser> users = new ArrayList<>();
    final Map<Long, List<RawSocialAccount>> userSocialAccounts = new HashMap<>();
    final List<RawPullRequest> pullRequests = new ArrayList<>();
    final List<RawIssue> issues = new ArrayList<>();
    final Map<Long, List<RawCodeReview>> pullRequestReviews = new HashMap<>();
    final Map<Long, List<RawCommit>> pullRequestCommits = new HashMap<>();
    final Map<Tuple, RawCheckRuns> checkRuns = new HashMap<>();
    final Map<Tuple, List<Long>> closingIssues = new HashMap<>();

    public static <T> T load(String path, Class<T> type) {
        final var inputStream = type.getResourceAsStream(path);
        try {
            return new ObjectMapper().readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<RawUser> user(Long userId) {
        return users.stream().filter(user -> user.getId().equals(userId)).findFirst();
    }

    @Override
    public List<RawSocialAccount> userSocialAccounts(Long userId) {
        return userSocialAccounts.getOrDefault(userId, new ArrayList<>());
    }

    @Override
    public Optional<RawPullRequest> pullRequest(String repoOwner, String repoName, Long prNumber) {
        return pullRequests.stream().filter(
                        pr -> pr.getBase().getRepo().getOwner().getLogin().equals(repoOwner) &&
                                pr.getBase().getRepo().getName().equals(repoName) &&
                                pr.getNumber().equals(prNumber))
                .findFirst();
    }

    @Override
    public Optional<RawIssue> issue(String repoOwner, String repoName, Long issueNumber) {
        return issues.stream().filter(
                        issue -> issue.getRepositoryUrl().endsWith(String.format("%s/%s", repoOwner, repoName)) &&
                                issue.getNumber().equals(issueNumber))
                .findFirst();
    }

    @Override
    public List<RawCodeReview> pullRequestReviews(Long pullRequestId) {
        return pullRequestReviews.getOrDefault(pullRequestId, new ArrayList<>());
    }

    @Override
    public List<RawCommit> pullRequestCommits(Long pullRequestId) {
        return pullRequestCommits.getOrDefault(pullRequestId, new ArrayList<>());
    }

    @Override
    public RawCheckRuns checkRuns(Long repoId, String sha) {
        return checkRuns.get(Tuple.tuple(repoId, sha));
    }

    @Override
    public List<Long> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        return closingIssues.getOrDefault(Tuple.tuple(repoOwner, repoName, pullRequestNumber), new ArrayList<>());
    }

    public void feedWith(RawUser... rawUsers) {
        Arrays.stream(rawUsers).sequential().forEach(this::saveUser);
    }

    public void feedWith(Long userId, RawSocialAccount... socialAccounts) {
        saveUserSocialAccounts(userId, Arrays.stream(socialAccounts).toList());
    }

    public void feedWith(RawPullRequest... pullRequests) {
        Arrays.stream(pullRequests).sequential().forEach(this::savePullRequest);
    }

    public void feedWith(RawIssue... issues) {
        Arrays.stream(issues).sequential().forEach(this::saveIssue);
    }

    public void feedWith(Long pullRequestId, RawCodeReview... codeReviews) {
        savePullRequestReviews(pullRequestId, Arrays.stream(codeReviews).toList());
    }

    public void feedWith(Long pullRequestId, RawCommit... commits) {
        savePullRequestCommits(pullRequestId, Arrays.stream(commits).toList());
    }

    public void feedWith(Long repoId, String sha, RawCheckRuns checkRuns) {
        saveCheckRuns(repoId, sha, checkRuns);
    }

    public void feedWith(String repoOwner, String repoName, Long pullRequestNumber, Long... issueNumbers) {
        saveClosingIssues(repoOwner, repoName, pullRequestNumber, Arrays.stream(issueNumbers).toList());
    }


    @Override
    public void saveUser(RawUser user) {
        users.add(user);
    }

    @Override
    public void saveUserSocialAccounts(Long userId, List<RawSocialAccount> socialAccounts) {
        userSocialAccounts.put(userId, socialAccounts);
    }

    @Override
    public void savePullRequest(RawPullRequest pullRequest) {
        pullRequests.add(pullRequest);
    }

    public void savePullRequestReviews(Long pullRequestId, List<RawCodeReview> codeReviews) {
        pullRequestReviews.put(pullRequestId, codeReviews);
    }

    @Override
    public void savePullRequestCommits(Long pullRequestId, List<RawCommit> commits) {
        pullRequestCommits.put(pullRequestId, commits);
    }

    @Override
    public void saveCheckRuns(Long repoId, String sha, RawCheckRuns checkRuns) {
        this.checkRuns.put(Tuple.tuple(repoId, sha), checkRuns);
    }

    @Override
    public void saveIssue(RawIssue issue) {
        issues.add(issue);
    }

    @Override
    public void saveClosingIssues(String repoOwner, String repoName, Long pullRequestId, List<Long> issueIds) {
        closingIssues.put(Tuple.tuple(repoOwner, repoName, pullRequestId), issueIds);
    }

    public List<RawUser> users() {
        return users;
    }

    public Map<Long, List<RawSocialAccount>> userSocialAccounts() {
        return userSocialAccounts;
    }

    public List<RawPullRequest> pullRequests() {
        return pullRequests;
    }

    public List<RawIssue> issues() {
        return issues;
    }

    public Map<Long, List<RawCodeReview>> codeReviews() {
        return pullRequestReviews;
    }

    public Map<Long, List<RawCommit>> commits() {
        return pullRequestCommits;
    }

    public Map<Tuple, RawCheckRuns> checkRuns() {
        return checkRuns;
    }

    public Map<Tuple, List<Long>> closingIssues() {
        return closingIssues;
    }
}
