package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.NewContributionNotifierJob;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.NotifierJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.ApiClient;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.NotifierJobStorage;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class NotifierJobManagerJobService implements NotifierJobManager {
    private final ContributionStorage contributionStorage;
    private final ApiClient apiClient;
    private final NotifierJobStorage notifierJobStorage;

    @Override
    public Set<Job> allJobs() {
        return Set.of(new NewContributionNotifierJob(contributionStorage, apiClient, notifierJobStorage));
    }
}
