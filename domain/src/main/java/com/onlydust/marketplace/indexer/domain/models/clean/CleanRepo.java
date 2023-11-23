package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawLanguages;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@Value
public class CleanRepo {
    Long id;
    String name;
    String htmlUrl;
    ZonedDateTime updatedAt;
    String description;
    CleanAccount owner;
    Long starsCount;
    Long forksCount;
    Boolean hasIssues;
    @Builder.Default
    Map<String, Long> languages = new HashMap<>();
    CleanRepo parent;
    String visibility;


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
                .visibility(Optional.ofNullable(repo.getVisibility())
                        .orElse(Boolean.TRUE.equals(repo.getIsPrivate()) ? "private" : "public"))
                .build();
    }

    public static CleanRepo of(RawRepo repo, CleanAccount owner, RawLanguages languages, CleanRepo parent) {
        return CleanRepo.of(repo, owner).toBuilder()
                .parent(parent)
                .languages(languages.get())
                .build();
    }
}
