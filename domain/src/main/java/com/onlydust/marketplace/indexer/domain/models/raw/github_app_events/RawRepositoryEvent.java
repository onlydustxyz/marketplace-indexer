package com.onlydust.marketplace.indexer.domain.models.raw.github_app_events;


import com.onlydust.marketplace.indexer.domain.models.raw.JsonDocument;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawRepositoryEvent extends JsonDocument {
    String action;
    RawRepo repository;
}
