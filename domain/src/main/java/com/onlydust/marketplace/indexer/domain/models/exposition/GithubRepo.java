package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.Date;

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

    public static GithubRepo of(CleanRepo repo) {
        return GithubRepo.builder()
                .id(repo.getId())
                .owner(GithubAccount.of(repo.getOwner()))
                .name(repo.getName())
                .htmlUrl(repo.getHtmlUrl())
                .updatedAt(repo.getUpdatedAt())
                .description(repo.getDescription())
                .starsCount(repo.getStarsCount())
                .forksCount(repo.getForksCount())
                .build();
    }
}