package com.onlydust.marketplace.indexer.domain.stubs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.groups.Tuple;

import java.io.IOException;
import java.util.*;

public class RawStorageRepositoryStub implements RawStorageRepository {
    final List<RawRepo> repos = new ArrayList<>();
    final List<RawUser> users = new ArrayList<>();
    final Map<Long, List<RawSocialAccount>> userSocialAccounts = new HashMap<>();
    final Map<Long, List<RawCodeReview>> pullRequestReviews = new HashMap<>();
    final Map<Long, List<RawCommit>> pullRequestCommits = new HashMap<>();
    final Map<Tuple, RawCheckRuns> checkRuns = new HashMap<>();
    final Map<Long, List<Long>> closingIssues = new HashMap<>();
    final Map<Long, List<RawPullRequest>> repoPullRequests = new HashMap<>();
    final Map<Long, List<RawIssue>> repoIssues = new HashMap<>();
    final Map<Long, RawLanguages> repoLanguages = new HashMap<>();

    public static <T> T load(String path, Class<T> type) {
        final var inputStream = type.getResourceAsStream(path);
        try {
            return new ObjectMapper().readValue(inputStream, type);
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
    public List<RawPullRequest> repoPullRequests(Long repoId) {
        return repoPullRequests.getOrDefault(repoId, new ArrayList<>());
    }

    @Override
    public List<RawIssue> repoIssues(Long repoId) {
        return repoIssues.getOrDefault(repoId, new ArrayList<>());
    }

    @Override
    public RawLanguages repoLanguages(Long repoId) {
        return repoLanguages.getOrDefault(repoId, new RawLanguages());
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
    public List<RawCodeReview> pullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return pullRequestReviews.getOrDefault(pullRequestId, new ArrayList<>());
    }

    @Override
    public List<RawCommit> pullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return pullRequestCommits.getOrDefault(pullRequestId, new ArrayList<>());
    }

    @Override
    public RawCheckRuns checkRuns(Long repoId, String sha) {
        return checkRuns.get(Tuple.tuple(repoId, sha));
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        return repo(repoOwner, repoName).flatMap(repo ->
                pullRequest(repo.getId(), pullRequestNumber).map(pullRequest -> {
                    final var prClosingIssues = closingIssues.get(pullRequest.getId()).stream().map(issueId -> Pair.of(issueId, repoIssues.getOrDefault(repo.getId(), new ArrayList<>()).stream().filter(i -> i.getId().equals(issueId)).findFirst().orElseThrow().getNumber())).toList();
                    return new RawPullRequestClosingIssues(pullRequest.getId(), prClosingIssues);
                }));
    }

    public void feedWith(RawRepo... repos) {
        Arrays.stream(repos).sequential().forEach(this::saveRepo);
    }

    public void feedWith(Long repoId, RawLanguages languages) {
        saveRepoLanguages(repoId, languages);
    }

    public void feedWith(RawUser... users) {
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
    }

    public void feedWith(Long repoId, RawPullRequest... pullRequests) {
        saveRepoPullRequests(repoId, Arrays.stream(pullRequests).toList());
    }

    public void feedWith(Long repoId, RawIssue... issues) {
        saveRepoIssues(repoId, Arrays.stream(issues).toList());
    }

    public void feedWith(Long repoId, String sha, RawCheckRuns checkRuns) {
        saveCheckRuns(repoId, sha, checkRuns);
    }

    public void feedClosingIssuesWith(Long pullRequestId, RawIssue... issues) {
        saveClosingIssues(new RawPullRequestClosingIssues(pullRequestId, Arrays.stream(issues).map(issue -> Pair.of(issue.getId(), issue.getNumber())).toList()));
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
    public void savePullRequest(Long repoId, RawPullRequest pullRequest) {
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
    public void saveCheckRuns(Long repoId, String sha, RawCheckRuns checkRuns) {
        this.checkRuns.put(Tuple.tuple(repoId, sha), checkRuns);
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
    public void saveRepoPullRequests(Long repoId, List<RawPullRequest> pullRequests) {
        pullRequests.forEach(pullRequest -> savePullRequest(repoId, pullRequest));
    }

    @Override
    public void saveRepoIssues(Long repoId, List<RawIssue> issues) {
        issues.forEach(issue -> saveIssue(repoId, issue));
    }

    @Override
    public void saveRepoLanguages(Long repoId, RawLanguages languages) {
        repoLanguages.put(repoId, languages);
    }

    @Override
    public void saveClosingIssues(RawPullRequestClosingIssues closingIssues) {
        this.closingIssues.put(closingIssues.pullRequestId(), closingIssues.issueIdNumbers().stream().map(Pair::getLeft).toList());
    }

    public List<RawRepo> repos() {
        return repos;
    }

    public List<RawUser> users() {
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

    public Map<Tuple, RawCheckRuns> checkRuns() {
        return checkRuns;
    }

    public Map<Long, List<Long>> closingIssues() {
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
