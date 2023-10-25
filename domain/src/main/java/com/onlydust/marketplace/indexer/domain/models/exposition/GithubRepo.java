package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.Map;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class GithubRepo {
    Long id;
    GithubAccount owner;
    String name;
    String htmlUrl;
    Date updatedAt;
    String description;
    Long starsCount;
    Long forksCount;
    Boolean hasIssues;
    Map<String, Long> languages;
    GithubRepo parent;


    public static GithubRepo of(CleanRepo repo) {
        return of(repo, GithubAccount.of(repo.getOwner()));
    }

    public static GithubRepo of(CleanRepo repo, GithubAccount owner) {
        return GithubRepo.builder()
                .id(repo.getId())
                .owner(owner)
                .name(repo.getName())
                .htmlUrl(repo.getHtmlUrl())
                .updatedAt(repo.getUpdatedAt())
                .description(repo.getDescription())
                .starsCount(repo.getStarsCount())
                .forksCount(repo.getForksCount())
                .hasIssues(repo.getHasIssues())
                .languages(repo.getLanguages())
                .parent(repo.getParent() == null ? null : GithubRepo.of(repo.getParent()))
                .build();
    }
}
