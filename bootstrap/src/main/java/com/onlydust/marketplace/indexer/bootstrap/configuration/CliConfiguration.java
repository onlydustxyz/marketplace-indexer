package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.cli.CommitIndexerCliAdapter;
import com.onlydust.marketplace.indexer.cli.RepoRefreshCliAdapter;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubPullRequestRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.PullRequestCommitsRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.PullRequestRepository;
import lombok.NonNull;
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
    public CommitIndexerCliAdapter commitIndexerCliAdapter(final @NonNull PullRequestCommitsRepository pullRequestCommitsRepository,
                                                           final @NonNull PullRequestRepository pullRequestRepository,
                                                           final @NonNull RawStorageReader githubRawStorageReader,
                                                           final @NonNull RepoIndexingJobEntityRepository repoIndexingJobEntityRepository,
                                                           final @NonNull GithubAppContext githubAppContext,
                                                           final @NonNull GithubPullRequestRepository githubPullRequestRepository
    ) {
        return new CommitIndexerCliAdapter(pullRequestCommitsRepository, pullRequestRepository, githubRawStorageReader, repoIndexingJobEntityRepository,
                githubAppContext, githubPullRequestRepository);
    }
}
