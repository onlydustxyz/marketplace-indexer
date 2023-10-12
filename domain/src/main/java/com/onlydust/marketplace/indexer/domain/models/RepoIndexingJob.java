package com.onlydust.marketplace.indexer.domain.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class RepoIndexingJob extends Job {
    Long installationId;
    Set<Long> repos;
}
