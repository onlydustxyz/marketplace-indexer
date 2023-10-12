package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.indexer.infrastructure.quartz.adapters.RepoIndexingJobScheduler;
import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CronJobConfiguration {
    @Bean
    RepoIndexingJobScheduler repoIndexingJobScheduler(final Scheduler scheduler) {
        return new RepoIndexingJobScheduler(scheduler);
    }
}
