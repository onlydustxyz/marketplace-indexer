package com.onlydust.marketplace.indexer.domain.stubs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.model.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageRepository;

import java.io.IOException;
import java.util.*;

public class RawStorageRepositoryStub implements RawStorageRepository {
    final List<RawUser> users = new ArrayList<>();
    final Map<Integer, List<RawSocialAccount>> userSocialAccounts = new HashMap<>();
    final List<RawPullRequest> pullRequests = new ArrayList<>();
    final Map<Integer, List<RawCodeReview>> pullRequestReviews = new HashMap<>();
    final Map<Integer, List<RawCommit>> pullRequestCommits = new HashMap<>();

    public static <T> T load(String path, Class<T> type) {
        final var inputStream = type.getResourceAsStream(path);
        try {
            return new ObjectMapper().readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<RawUser> user(Integer userId) {
        return users.stream().filter(user -> user.getId().equals(userId)).findFirst();
    }

    @Override
    public List<RawSocialAccount> userSocialAccounts(Integer userId) {
        return userSocialAccounts.getOrDefault(userId, new ArrayList<>());
    }

    @Override
    public Optional<RawPullRequest> pullRequest(String repoOwner, String repoName, Integer prNumber) {
        return pullRequests.stream().filter(
                        pr -> pr.getBase().getRepo().getOwner().getLogin().equals(repoOwner) &&
                                pr.getBase().getRepo().getName().equals(repoName) &&
                                pr.getNumber().equals(prNumber))
                .findFirst();
    }

    @Override
    public List<RawCodeReview> pullRequestReviews(Integer pullRequestId) {
        return pullRequestReviews.getOrDefault(pullRequestId, new ArrayList<>());
    }

    @Override
    public List<RawCommit> pullRequestCommits(Integer pullRequestId) {
        return pullRequestCommits.getOrDefault(pullRequestId, new ArrayList<>());
    }

    public void feedWith(RawUser... rawUsers) {
        Arrays.stream(rawUsers).sequential().forEach(this::saveUser);
    }

    public void feedWith(Integer userId, RawSocialAccount... socialAccounts) {
        saveUserSocialAccounts(userId, Arrays.stream(socialAccounts).toList());
    }

    public void feedWith(RawPullRequest... pullRequests) {
        Arrays.stream(pullRequests).sequential().forEach(this::savePullRequest);
    }

    public void feedWith(Integer pullRequestId, RawCodeReview... codeReviews) {
        savePullRequestReviews(pullRequestId, Arrays.stream(codeReviews).toList());
    }

    public void feedWith(Integer pullRequestId, RawCommit... commits) {
        savePullRequestCommits(pullRequestId, Arrays.stream(commits).toList());
    }

    @Override
    public void saveUser(RawUser user) {
        users.add(user);
    }

    @Override
    public void saveUserSocialAccounts(Integer userId, List<RawSocialAccount> socialAccounts) {
        userSocialAccounts.put(userId, socialAccounts);
    }

    @Override
    public void savePullRequest(RawPullRequest pullRequest) {
        pullRequests.add(pullRequest);
    }

    public void savePullRequestReviews(Integer pullRequestId, List<RawCodeReview> codeReviews) {
        pullRequestReviews.put(pullRequestId, codeReviews);
    }

    @Override
    public void savePullRequestCommits(Integer pullRequestId, List<RawCommit> commits) {
        pullRequestCommits.put(pullRequestId, commits);
    }

    public List<RawUser> users() {
        return users;
    }

    public Map<Integer, List<RawSocialAccount>> userSocialAccounts() {
        return userSocialAccounts;
    }

    public List<RawPullRequest> pullRequests() {
        return pullRequests;
    }

    public Map<Integer, List<RawCodeReview>> codeReviews() {
        return pullRequestReviews;
    }

    public Map<Integer, List<RawCommit>> commits() {
        return pullRequestCommits;
    }
}
