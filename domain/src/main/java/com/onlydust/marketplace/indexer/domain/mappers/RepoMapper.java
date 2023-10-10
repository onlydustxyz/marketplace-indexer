package com.onlydust.marketplace.indexer.domain.mappers;

import com.onlydust.marketplace.indexer.domain.models.clean.Issue;
import com.onlydust.marketplace.indexer.domain.models.clean.PullRequest;
import com.onlydust.marketplace.indexer.domain.models.clean.Repo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawLanguages;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;

import java.util.ArrayList;
import java.util.List;

public class RepoMapper {
    public static Repo map(RawRepo repo) {
        return map(repo, new ArrayList<>(), new ArrayList<>(), new RawLanguages());
    }

    public static Repo map(RawRepo repo, List<PullRequest> pullRequests, List<Issue> issues, RawLanguages languages) {
        return new Repo(
                repo.getId(),
                repo.getName(),
                repo.getHtmlUrl(),
                repo.getUpdatedAt(),
                repo.getDescription(),
                repo.getStargazersCount(),
                repo.getForksCount(),
                pullRequests,
                issues,
                languages.get());
    }
}
