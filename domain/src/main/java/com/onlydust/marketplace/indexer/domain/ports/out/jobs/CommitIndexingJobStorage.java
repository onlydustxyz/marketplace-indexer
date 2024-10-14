package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import com.onlydust.marketplace.indexer.domain.models.CommitIndexingJobItem;

import java.util.List;

public interface CommitIndexingJobStorage {
    List<CommitIndexingJobItem> commitsForLeastIndexedUsers(int limit);

    List<CommitIndexingJobItem> all();
}
