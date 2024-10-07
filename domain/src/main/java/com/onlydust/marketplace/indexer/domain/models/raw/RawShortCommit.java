package com.onlydust.marketplace.indexer.domain.models.raw;


import lombok.*;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
@AllArgsConstructor
public class RawShortCommit extends JsonDocument {
    @NonNull
    String sha;
    Author author;
    String message;
    Boolean distinct;
    String url;

    @Value
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor(force = true)
    @ToString(callSuper = true)
    @AllArgsConstructor
    public static class Author extends JsonDocument {
        @NonNull
        String name;
        @NonNull
        String email;
    }
}
