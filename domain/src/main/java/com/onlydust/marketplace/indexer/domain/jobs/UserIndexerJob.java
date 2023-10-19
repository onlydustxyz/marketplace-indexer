package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@AllArgsConstructor
@Slf4j
public class UserIndexerJob extends Job {
    final UserIndexer userIndexer;
    final Set<Long> userIds;

    @Override
    public void execute() {
        LOGGER.info("Indexing users {}", userIds);
        userIds.forEach(userIndexer::indexUser);
    }

    @Override
    public String name() {
        return "user-indexer";
    }
}
