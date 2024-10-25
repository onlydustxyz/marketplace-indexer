package com.onlydust.marketplace.indexer.domain.stubs;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import lombok.NonNull;
import org.assertj.core.groups.Tuple;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class RawStorageWriterStub implements RawStorageWriter, RawStorageReader {
    final List<RawRepo> repos = new ArrayList<>();
    final List<RawAccount> users = new ArrayList<>();
    final Map<Long, List<RawSocialAccount>> userSocialAccounts = new HashMap<>();
    final Map<Long, List<RawCodeReview>> pullRequestReviews = new HashMap<>();
    final Map<Long, List<RawCommit>> pullRequestCommits = new HashMap<>();
    final Map<Tuple, RawPullRequestClosingIssues> closingIssues = new HashMap<>();
    final Map<Long, List<RawPullRequest>> repoPullRequests = new HashMap<>();
    final Map<Long, List<RawIssue>> repoIssues = new HashMap<>();
    final Map<Long, RawLanguages> repoLanguages = new HashMap<>();
    final List<RawCommit> commits = new ArrayList<>();

    public static <T> T load(String path, Class<T> type) {
        final var inputStream = type.getResourceAsStream(path);
        try {
            return JsonMapper.builder().findAndAddModules().build().readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<RawRepo> repo(Long repoId) {
        return repos.stream().filter(repo -> repo.getId().equals(repoId)).findFirst();
    }

    @Override
    public Optional<RawRepo> repo(String repoOwner, String repoName) {
        return repos.stream().filter(repo -> repo.getOwner().getLogin().equals(repoOwner) && repo.getName().equals(repoName)).findFirst();
    }

    @Override
    public Stream<RawPullRequest> repoPullRequests(Long repoId) {
        return repoPullRequests.getOrDefault(repoId, new ArrayList<>()).stream();
    }

    @Override
    public Stream<RawIssue> repoIssues(Long repoId) {
        return repoIssues.getOrDefault(repoId, new ArrayList<>()).stream();
    }

    @Override
    public Optional<RawLanguages> repoLanguages(Long repoId) {
        return Optional.of(repoLanguages.getOrDefault(repoId, new RawLanguages()));
    }

    @Override
    public Optional<RawAccount> user(Long userId) {
        return users.stream().filter(user -> user.getId().equals(userId)).findFirst();
    }

    @Override
    public Optional<List<RawSocialAccount>> userSocialAccounts(Long userId) {
        return Optional.of(userSocialAccounts.getOrDefault(userId, List.of()));
    }

    @Override
    public Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber) {
        return repoPullRequests.getOrDefault(repoId, new ArrayList<>())
                .stream().filter(pr -> pr.getNumber().equals(prNumber))
                .findFirst();
    }

    @Override
    public Optional<RawIssue> issue(Long repoId, Long issueNumber) {
        return repoIssues.getOrDefault(repoId, new ArrayList<>())
                .stream().filter(issue -> issue.getNumber().equals(issueNumber))
                .findFirst();
    }

    @Override
    public Optional<List<RawCodeReview>> pullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return Optional.of(pullRequestReviews.getOrDefault(pullRequestId, new ArrayList<>()));
    }

    @Override
    public Optional<List<RawCommit>> pullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return Optional.of(pullRequestCommits.getOrDefault(pullRequestId, new ArrayList<>()));
    }

    @Override
    public Optional<RawCommit> commit(Long repoId, String sha) {
        return commits.stream().filter(commit -> commit.getSha().equals(sha)).findFirst();
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        return Optional.ofNullable(closingIssues.get(Tuple.tuple(repoOwner, repoName, pullRequestNumber)));
    }

    public void feedWith(RawRepo... repos) {
        Arrays.stream(repos).sequential().forEach(this::saveRepo);
    }

    public void feedWith(Long repoId, RawLanguages languages) {
        saveRepoLanguages(repoId, languages);
    }

    public void feedWith(RawAccount... users) {
        Arrays.stream(users).sequential().forEach(this::saveUser);
    }

    public void feedWith(Long userId, RawSocialAccount... socialAccounts) {
        saveUserSocialAccounts(userId, Arrays.stream(socialAccounts).toList());
    }

    public void feedWith(Long pullRequestId, RawCodeReview... codeReviews) {
        savePullRequestReviews(pullRequestId, Arrays.stream(codeReviews).toList());
    }

    public void feedWith(Long pullRequestId, RawCommit... commits) {
        savePullRequestCommits(pullRequestId, Arrays.stream(commits).toList());
        Arrays.stream(commits).forEach(c -> saveCommit(0L, c));
    }

    public void feedWith(RawPullRequest... pullRequests) {
        Arrays.stream(pullRequests).forEach(this::savePullRequest);
    }

    public void feedWith(Long repoId, RawIssue... issues) {
        Arrays.stream(issues).forEach(issue -> saveIssue(repoId, issue));
    }

    public void feedWith(String repoOwner, String repoName, Long pullRequestNumber, RawPullRequestClosingIssues closingIssues) {
        this.closingIssues.put(Tuple.tuple(repoOwner, repoName, pullRequestNumber), closingIssues);
    }

    @Override
    public void saveUser(RawAccount user) {
        users.add(user);
    }

    @Override
    public void saveUserSocialAccounts(Long userId, List<RawSocialAccount> socialAccounts) {
        userSocialAccounts.put(userId, socialAccounts);
    }

    @Override
    public void savePullRequest(RawPullRequest pullRequest) {
        final var repoId = pullRequest.getBase().getRepo().getId();
        final var prs = repoPullRequests.getOrDefault(repoId, new ArrayList<>());
        prs.add(pullRequest);
        repoPullRequests.put(repoId, prs);
    }

    public void savePullRequestReviews(Long pullRequestId, List<RawCodeReview> codeReviews) {
        pullRequestReviews.put(pullRequestId, codeReviews);
    }

    @Override
    public void savePullRequestCommits(Long pullRequestId, List<RawCommit> commits) {
        pullRequestCommits.put(pullRequestId, commits);
    }

    @Override
    public void saveIssue(Long repoId, RawIssue issue) {
        final var i = repoIssues.getOrDefault(repoId, new ArrayList<>());
        i.add(issue);
        repoIssues.put(repoId, i);
    }

    @Override
    public void saveRepo(RawRepo repo) {
        repos.add(repo);
    }

    @Override
    public void deleteRepo(Long repoId) {
        repos.removeIf(repo -> repo.getId().equals(repoId));
    }

    @Override
    public void saveRepoLanguages(Long repoId, RawLanguages languages) {
        repoLanguages.put(repoId, languages);
    }

    @Override
    public void saveClosingIssues(String repoOwner, String repoName, Long pullRequestNumber, RawPullRequestClosingIssues closingIssues) {
        this.closingIssues.put(Tuple.tuple(repoOwner, repoName, pullRequestNumber), closingIssues);
    }

    @Override
    public void deleteIssue(Long id) {
        repoIssues.values().forEach(issues -> issues.removeIf(issue -> issue.getId().equals(id)));
    }

    @Override
    public void saveCommit(@NonNull Long repoId, @NonNull RawCommit commit) {
        commits.add(commit);
    }

    @Override
    public void saveCommits(@NonNull Long repoId, @NonNull List<RawShortCommit> commits) {

    }

    public List<RawRepo> repos() {
        return repos;
    }

    public List<RawAccount> users() {
        return users;
    }

    public Map<Long, List<RawSocialAccount>> userSocialAccounts() {
        return userSocialAccounts;
    }

    public Map<Long, List<RawCodeReview>> codeReviews() {
        return pullRequestReviews;
    }

    public Map<Long, List<RawCommit>> commits() {
        return pullRequestCommits;
    }

    public Map<Tuple, RawPullRequestClosingIssues> closingIssues() {
        return closingIssues;
    }

    public Map<Long, List<RawPullRequest>> repoPullRequests() {
        return repoPullRequests;
    }

    public Map<Long, List<RawIssue>> repoIssues() {
        return repoIssues;
    }

    public Map<Long, RawLanguages> repoLanguages() {
        return repoLanguages;
    }
}
