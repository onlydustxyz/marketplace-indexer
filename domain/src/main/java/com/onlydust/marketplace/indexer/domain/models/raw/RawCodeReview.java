package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.Date;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawCodeReview extends JsonDocument {
    Long id;
    String state;
    @JsonProperty("submitted_at")
    Date submittedAt;

    @JsonProperty("user")
    RawAccount author;
}
