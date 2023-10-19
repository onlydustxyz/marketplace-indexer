package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.indexer.infrastructure.quartz.adapters.QuartzRepoRefresherJob;
import com.onlydust.indexer.infrastructure.quartz.adapters.QuartzUserRefresherJob;
import com.onlydust.marketplace.indexer.domain.ports.in.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CronJobConfiguration {
    @Bean
    QuartzRepoRefresherJob repoIndexingJobScheduler(final Scheduler scheduler, RepoIndexer repoIndexer) {
        return new QuartzRepoRefresherJob(scheduler, repoIndexer);
    }

    @Bean
    QuartzUserRefresherJob userIndexingJobScheduler(final Scheduler scheduler, UserIndexer userIndexer) {
        return new QuartzUserRefresherJob(scheduler, userIndexer);
    }
}
