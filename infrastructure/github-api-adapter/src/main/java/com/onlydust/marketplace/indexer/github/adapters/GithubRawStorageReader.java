package com.onlydust.marketplace.indexer.github.adapters;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import com.onlydust.marketplace.indexer.github.GithubPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@Slf4j
public class GithubRawStorageReader implements RawStorageReader {
    private final GithubHttpClient client;

    @Override
    public Optional<RawRepo> repo(Long repoId) {
        return client.get("/repositories/" + repoId, RawRepo.class);
    }

    @Override
    public Optional<RawRepo> repo(String repoOwner, String repoName) {
        return client.get("/repos/" + repoOwner + "/" + repoName, RawRepo.class);
    }

    @Override
    public Stream<RawPullRequest> repoPullRequests(Long repoId) {
        final var page = new GithubPage<>(client, "/repositories/" + repoId + "/pulls?state=all&sort=updated&per_page=100", RawPullRequest[].class);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(page, Spliterator.ORDERED), false);
    }

    @Override
    public Stream<RawIssue> repoIssues(Long repoId) {
        final var page = new GithubPage<>(client, "/repositories/" + repoId + "/issues?state=all&sort=updated&per_page=100", RawIssue[].class);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(page, Spliterator.ORDERED), false);
    }

    @Override
    public RawLanguages repoLanguages(Long repoId) {
        return client.get("/repositories/" + repoId + "/languages", RawLanguages.class)
                .orElse(new RawLanguages());
    }

    @Override
    public Optional<RawAccount> user(Long userId) {
        return client.get("/user/" + userId, RawAccount.class);
    }

    @Override
    public List<RawSocialAccount> userSocialAccounts(Long userId) {
        return client.get("/user/" + userId + "/social_accounts", RawSocialAccount[].class)
                .map(Arrays::asList)
                .orElse(List.of());
    }

    @Override
    public Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber) {
        return client.get("/repositories/" + repoId + "/pulls/" + prNumber, RawPullRequest.class);
    }

    @Override
    public Optional<RawIssue> issue(Long repoId, Long issueNumber) {
        return client.get("/repositories/" + repoId + "/issues/" + issueNumber, RawIssue.class);
    }

    @Override
    public List<RawCodeReview> pullRequestReviews(Long repoId, Long pullRequestId, Long prNumber) {
        return client.get("/repositories/" + repoId + "/pulls/" + prNumber + "/reviews", RawCodeReview[].class)
                .map(Arrays::asList)
                .orElse(List.of());
    }

    @Override
    public List<RawCommit> pullRequestCommits(Long repoId, Long pullRequestId, Long prNumber) {
        return client.get("/repositories/" + repoId + "/pulls/" + prNumber + "/commits", RawCommit[].class)
                .map(Arrays::asList)
                .orElse(List.of());
    }

    @Override
    public Optional<RawCheckRuns> checkRuns(Long repoId, String sha) {
        return client.get("/repositories/" + repoId + "/commits/" + sha + "/check-runs", RawCheckRuns.class);
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        final var query = "query GetClosingIssues($owner: String!, $name: String!, $number: Int!) { repository(owner: $owner, name: $name) { pullRequest(number: $number) { databaseId closingIssuesReferences(first: 10) { nodes { databaseId number } } } } }";

        final var variables = Map.of(
                "owner", repoOwner,
                "name", repoName,
                "number", pullRequestNumber
        );

        return client.graphql(query, variables).map(
                response -> {
                    var issues = new ArrayList<Pair<Long, Long>>();
                    final var pullRequest = response.at("/data/repository/pullRequest");

                    for (var node : pullRequest.at("/closingIssuesReferences/nodes")) {
                        issues.add(Pair.of(node.get("databaseId").asLong(), node.get("number").asLong()));
                    }

                    return new RawPullRequestClosingIssues(pullRequest.get("databaseId").asLong(), issues);
                }
        );
    }
}
