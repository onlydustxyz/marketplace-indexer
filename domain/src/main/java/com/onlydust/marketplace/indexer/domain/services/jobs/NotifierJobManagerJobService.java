package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.NewContributionNotifierJob;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.ApiClient;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.NotifierJobStorage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotifierJobManagerJobService implements JobManager {
    private final ContributionStorage contributionStorage;
    private final ApiClient apiClient;
    private final NotifierJobStorage notifierJobStorage;

    @Override
    public Job createJob() {
        return new NewContributionNotifierJob(contributionStorage, apiClient, notifierJobStorage);
    }
}
