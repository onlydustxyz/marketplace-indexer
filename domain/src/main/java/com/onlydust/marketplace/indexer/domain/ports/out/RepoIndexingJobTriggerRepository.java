package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;

import java.util.List;

public interface RepoIndexingJobTriggerRepository {
    List<RepoIndexingJobTrigger> list();
}