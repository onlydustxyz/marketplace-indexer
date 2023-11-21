package com.onlydust.marketplace.indexer.cli;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoRefreshJobManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;

@AllArgsConstructor
@Slf4j
@Profile("cli")
public class RepoRefreshCliAdapter implements CommandLineRunner {
    private final TaskExecutor applicationTaskExecutor;
    private final RepoRefreshJobManager cachedRepoRefreshJobManager;

    @Override
    public void run(String... args) {
        LOGGER.info("Refreshing repos");
//        final var jobs = cachedRepoRefreshJobManager.allJobs().stream().map(j -> new FutureTask<>(j, null)).toList();
//        jobs.forEach(applicationTaskExecutor::execute);
//        jobs.forEach(j -> {
//            try {
//                j.get();
//            } catch (Exception e) {
//                throw OnlyDustException.internalServerError("Error refreshing expositions", e);
//            }
//        });
        cachedRepoRefreshJobManager.allJobs().forEach(Job::run);
    }
}
