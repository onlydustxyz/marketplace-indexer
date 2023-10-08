package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawCodeReview extends JsonDocument {
    Long id;

    @JsonProperty("user")
    RawUser author;
}
