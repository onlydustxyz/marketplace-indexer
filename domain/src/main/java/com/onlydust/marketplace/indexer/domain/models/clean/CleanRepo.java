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
    CleanAccount owner;
    Long starsCount;
    Long forksCount;
    @Builder.Default
    List<CleanPullRequest> pullRequests = new ArrayList<>();
    @Builder.Default
    List<CleanIssue> issues = new ArrayList<>();
    @Builder.Default
    Map<String, Long> languages = new HashMap<>();

    public static CleanRepo of(RawRepo repo, CleanAccount owner) {
        return CleanRepo
                .builder()
                .id(repo.getId())
                .name(repo.getName())
                .htmlUrl(repo.getHtmlUrl())
                .updatedAt(repo.getUpdatedAt())
                .description(repo.getDescription())
                .owner(owner)
                .starsCount(repo.getStargazersCount())
                .forksCount(repo.getForksCount())
                .build();
    }

    public static CleanRepo of(RawRepo repo, CleanAccount owner, List<CleanPullRequest> pullRequests, List<CleanIssue> issues, RawLanguages languages) {
        return CleanRepo.of(repo, owner).toBuilder()
                .pullRequests(pullRequests)
                .issues(issues)
                .languages(languages.get())
                .build();
    }
}
