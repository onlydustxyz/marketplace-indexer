package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJob;
import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.in.RefreshJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.out.JobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobTriggerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static java.util.stream.Collectors.*;

@AllArgsConstructor
@Slf4j
public class RepoRefreshJobService implements RefreshJobScheduler {
    private final RepoIndexingJobTriggerRepository repoIndexingJobTriggerRepository;
    private final JobScheduler<RepoIndexingJob> scheduler;

    @Override
    public void scheduleAllJobs() {
        repoIndexingJobTriggerRepository.list()
                .stream().collect(
                        groupingBy( // group triggers by installationId
                                RepoIndexingJobTrigger::installationId,
                                mapping(RepoIndexingJobTrigger::repoId, toSet())))
                .forEach(this::scheduleJob);
    }

    private void scheduleJob(Long installationId, Set<Long> repos) {
        scheduler.scheduleJob(RepoIndexingJob.builder().installationId(installationId).repos(repos).build());
    }
}
