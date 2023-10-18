package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawLanguages;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.*;

@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@Value
public class CleanRepo {
    Long id;
    String name;
    String htmlUrl;
    Date updatedAt;
    String description;
    Long starsCount;
    Long forksCount;
    @Builder.Default
    List<CleanPullRequest> pullRequests = new ArrayList<>();
    @Builder.Default
    List<CleanIssue> issues = new ArrayList<>();
    @Builder.Default
    Map<String, Long> languages = new HashMap<>();

    public static CleanRepo of(RawRepo repo) {
        return CleanRepo
                .builder()
                .id(repo.getId())
                .name(repo.getName())
                .htmlUrl(repo.getHtmlUrl())
                .updatedAt(repo.getUpdatedAt())
                .description(repo.getDescription())
                .starsCount(repo.getStargazersCount())
                .forksCount(repo.getForksCount())
                .build();
    }

    public static CleanRepo of(RawRepo repo, List<CleanPullRequest> pullRequests, List<CleanIssue> issues, RawLanguages languages) {
        return CleanRepo.of(repo).toBuilder()
                .pullRequests(pullRequests)
                .issues(issues)
                .languages(languages.get())
                .build();
    }
}
