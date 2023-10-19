package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class GithubCommit {
    GithubPullRequest pullRequest;
    GithubAccount author;

    public static GithubCommit of(CleanCommit commit, CleanPullRequest pullRequest) {
        return GithubCommit.builder()
                .pullRequest(GithubPullRequest.of(pullRequest))
                .author(GithubAccount.of(commit.getAuthor()))
                .build();
    }
}
