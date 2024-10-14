package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class GithubCommit {
    String sha;
    GithubAccount author;

    public static GithubCommit of(CleanCommit commit) {
        return GithubCommit.builder()
                .sha(commit.getSha())
                .author(commit.getAuthor() == null ? null : GithubAccount.of(commit.getAuthor()))
                .build();
    }

    public Optional<Long> getAuthorId() {
        return Optional.ofNullable(author).flatMap(a -> Optional.ofNullable(a.getId()));
    }
}
