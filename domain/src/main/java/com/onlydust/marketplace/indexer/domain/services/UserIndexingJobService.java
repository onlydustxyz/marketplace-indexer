package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.models.UserIndexingJob;
import com.onlydust.marketplace.indexer.domain.models.UserIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.JobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.out.UserIndexingJobTriggerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
@Slf4j
public class UserIndexingJobService {
    private final UserIndexingJobTriggerRepository userIndexingJobTriggerRepository;
    private final JobScheduler<UserIndexingJob> scheduler;

    public void scheduleAllJobs() {
        final var users = userIndexingJobTriggerRepository.list().stream()
                .map(UserIndexingJobTrigger::userId).collect(toSet());
        scheduler.scheduleJob(UserIndexingJob.builder().users(users).build());
    }
}
