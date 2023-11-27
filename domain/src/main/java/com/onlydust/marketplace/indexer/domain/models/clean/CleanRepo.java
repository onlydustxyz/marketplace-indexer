package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawLanguages;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    Boolean hasIssues;
    @Builder.Default
    Map<String, Long> languages = new HashMap<>();
    CleanRepo parent;
    Boolean isPublic;


    public static CleanRepo of(RawRepo repo, CleanAccount owner) {
        return CleanRepo
                .builder()
                .id(repo.getId())
                .name(repo.getName())
                .htmlUrl(repo.getHtmlUrl() == null ? owner.getHtmlUrl() + "/" + repo.getName() : repo.getHtmlUrl())
                .updatedAt(repo.getUpdatedAt())
                .description(repo.getDescription())
                .owner(owner)
                .starsCount(repo.getStargazersCount())
                .forksCount(repo.getForksCount())
                .hasIssues(repo.getHasIssues())
                .isPublic("public".equals(repo.getVisibility()) || Boolean.FALSE.equals(repo.getIsPrivate()))
                .build();
    }

    public static CleanRepo of(RawRepo repo, CleanAccount owner, RawLanguages languages, CleanRepo parent) {
        return CleanRepo.of(repo, owner).toBuilder()
                .parent(parent)
                .languages(languages.get())
                .build();
    }
}
