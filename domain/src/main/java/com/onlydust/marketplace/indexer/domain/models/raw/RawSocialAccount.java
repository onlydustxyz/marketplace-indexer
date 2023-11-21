package com.onlydust.marketplace.indexer.domain.models.raw;


import lombok.*;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ToString(callSuper = true)
public class RawSocialAccount extends JsonDocument {
    String provider;
    String url;
}
