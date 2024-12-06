package com.onlydust.marketplace.indexer.domain.models.raw.public_events;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RawIssuesEventPayload extends RawPublicEvent.Payload {
    String action;
    RawIssue issue;
}
