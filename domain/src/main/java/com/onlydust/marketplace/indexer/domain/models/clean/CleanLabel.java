package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawLabel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@Value
public class CleanLabel {
    @NonNull Long id;
    @NonNull String name;
    String description;

    public static CleanLabel of(RawLabel label) {
        return CleanLabel.builder()
                .id(label.getId())
                .name(label.getName())
                .description(label.getDescription())
                .build();
    }
}
