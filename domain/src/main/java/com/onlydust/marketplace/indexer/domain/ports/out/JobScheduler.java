package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.Job;

public interface JobScheduler<J extends Job> {
    void scheduleJob(final J job);
}
