package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.cli.RepoRefreshCliAdapter;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;

@Configuration
@Profile("cli")
public class CliConfiguration {
    @Bean
    public RepoRefreshCliAdapter repoRefreshCliAdapter(
            final TaskExecutor applicationTaskExecutor,
            final JobManager cacheOnlyRepoRefreshJobManager
    ) {
        return new RepoRefreshCliAdapter(applicationTaskExecutor, cacheOnlyRepoRefreshJobManager);
    }
}
