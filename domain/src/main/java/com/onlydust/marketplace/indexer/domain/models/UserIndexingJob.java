package com.onlydust.marketplace.indexer.domain.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.Set;

@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
@Value
public class UserIndexingJob extends Job {
    Set<Long> users;
}
