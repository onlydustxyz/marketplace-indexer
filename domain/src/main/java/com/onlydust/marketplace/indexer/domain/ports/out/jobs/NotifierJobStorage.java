package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import com.onlydust.marketplace.indexer.domain.models.NotifierJob;

public interface NotifierJobStorage {
    NotifierJob startJob();

    void endJob(NotifierJob job);

    void failJob(NotifierJob job);
}
