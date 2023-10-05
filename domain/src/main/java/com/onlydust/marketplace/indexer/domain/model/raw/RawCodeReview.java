package com.onlydust.marketplace.indexer.domain.model.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class RawCodeReview extends JsonDocument {
    Integer id;

    @JsonProperty("user")
    RawUser author;
}
