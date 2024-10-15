package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.models.CommitIndexingJobItem;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.CommitIndexer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class CommitIndexerJob extends Job {
    private final CommitIndexer commitIndexer;
    private final List<CommitIndexingJobItem> items;

    @Override
    public void execute() {
        LOGGER.info("Indexing {} commits", items.size());
        items.forEach(this::index);
    }

    private void index(final @NonNull CommitIndexingJobItem item) {
        try {
            LOGGER.debug("Indexing commit {}/{}", item.repoId(), item.sha());
            commitIndexer.indexCommit(item.repoId(), item.sha());
        } catch (Throwable e) {
            LOGGER.error("Failed to index commit {}/{} ", item.repoId(), item.sha(), e);
        }
    }

    @Override
    public String name() {
        return "commit-indexer";
    }
}
