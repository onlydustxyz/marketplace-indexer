package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.jobs.Job;

import java.util.List;

public interface UserRefreshJobManager {
    void addUserToRefresh(Long userId);

    List<Job> allJobs();
}
