package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.indexer.infrastructure.quartz.adapters.RepoIndexingJobScheduler;
import com.onlydust.indexer.infrastructure.quartz.adapters.UserIndexingJobScheduler;
import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CronJobConfiguration {
    @Bean
    RepoIndexingJobScheduler repoIndexingJobScheduler(final Scheduler scheduler) {
        return new RepoIndexingJobScheduler(scheduler);
    }

    @Bean
    UserIndexingJobScheduler userIndexingJobScheduler(final Scheduler scheduler) {
        return new UserIndexingJobScheduler(scheduler);
    }
}
