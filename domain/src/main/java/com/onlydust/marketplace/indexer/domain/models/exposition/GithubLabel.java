package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanLabel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
public class GithubLabel {
    @NonNull Long id;
    @NonNull String name;
    String description;

    public static GithubLabel of(CleanLabel label) {
        return GithubLabel.builder()
                .id(label.getId())
                .name(label.getName())
                .description(label.getDescription())
                .build();
    }
}
