package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.cli.CommitRefreshCliAdapter;
import com.onlydust.marketplace.indexer.cli.RepoRefreshCliAdapter;
import com.onlydust.marketplace.indexer.cli.UserPublicEventIndexerCliAdapter;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserPublicEventsIndexingJobManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cli")
public class CliConfiguration {
    @Bean
    public RepoRefreshCliAdapter repoRefreshCliAdapter(
            final JobManager cacheOnlyRepoRefreshJobManager
    ) {
        return new RepoRefreshCliAdapter(cacheOnlyRepoRefreshJobManager);
    }

    @Bean
    public CommitRefreshCliAdapter commitRefreshCliAdapter(
            final JobManager cacheOnlyCommitRefreshJobManager
    ) {
        return new CommitRefreshCliAdapter(cacheOnlyCommitRefreshJobManager);
    }

    @Bean
    public UserPublicEventIndexerCliAdapter userPublicEventIndexerCliAdapter(
            final UserPublicEventsIndexingJobManager userPublicEventsIndexingJobManager
    ) {
        return new UserPublicEventIndexerCliAdapter(userPublicEventsIndexingJobManager);
    }
}
