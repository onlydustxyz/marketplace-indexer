package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.CommitIndexingJobItem;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.CommitIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.CommitRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PostgresCommitIndexingJobStorage implements CommitIndexingJobStorage {
    final CommitRepository commitRepository;

    @Override
    public List<CommitIndexingJobItem> commitsForLeastIndexedUsers(int limit) {
        return commitRepository.findAllForLeastIndexedUsers(limit).stream()
                .map(c -> new CommitIndexingJobItem(c.getRepoId(), c.getSha()))
                .toList();
    }

    @Override
    public List<CommitIndexingJobItem> all() {
        return commitRepository.findAll().stream()
                .map(c -> new CommitIndexingJobItem(c.getRepoId(), c.getSha()))
                .toList();
    }
}
