package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.*;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.GithubAppInstallationStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorageComposite;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.CacheReadRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.CacheWriteRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.services.events.InstallationEventProcessorService;
import com.onlydust.marketplace.indexer.domain.services.jobs.RepoRefreshJobService;
import com.onlydust.marketplace.indexer.domain.services.jobs.UserRefreshJobService;
import com.onlydust.marketplace.indexer.domain.services.exposers.IssueContributionExposer;
import com.onlydust.marketplace.indexer.domain.services.exposers.PullRequestContributionExposer;
import com.onlydust.marketplace.indexer.domain.services.guards.RateLimitGuardedFullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.services.indexers.*;
import com.onlydust.marketplace.indexer.domain.services.monitoring.MonitoredFullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.services.monitoring.MonitoredIssueIndexer;
import com.onlydust.marketplace.indexer.domain.services.monitoring.MonitoredPullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.services.monitoring.MonitoredUserIndexer;
import com.onlydust.marketplace.indexer.github.GithubConfig;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import com.onlydust.marketplace.indexer.github.adapters.GithubAppJwtProvider;
import com.onlydust.marketplace.indexer.github.adapters.GithubRateLimitServiceAdapter;
import com.onlydust.marketplace.indexer.github.adapters.GithubRawStorageReader;
import com.onlydust.marketplace.indexer.postgres.adapters.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {
    @Bean
    @ConfigurationProperties("infrastructure.github")
    GithubConfig githubConfig() {
        return new GithubConfig();
    }

    @Bean
    @ConfigurationProperties("infrastructure.github-app")
    GithubAppJwtProvider.Config githubAppConfig() {
        return new GithubAppJwtProvider.Config();
    }

    @Bean
    @ConfigurationProperties("infrastructure.github.rate-limit")
    GithubRateLimitServiceAdapter.Config githubrateLimitConfig() {
        return new GithubRateLimitServiceAdapter.Config();
    }

    @Bean
    GithubRateLimitServiceAdapter githubRateLimitServiceAdapter(final GithubHttpClient githubHttpClient) {
        return new GithubRateLimitServiceAdapter(githubHttpClient);
    }

    @Bean
    RawStorageReader cachedRawStorageReader(
            final GithubRawStorageReader githubRawStorageReader,
            final PostgresRawStorageWriter postgresRawStorageRepository
    ) {
        return CacheReadRawStorageReaderDecorator.builder()
                .fetcher(CacheWriteRawStorageReaderDecorator.builder()
                        .fetcher(githubRawStorageReader)
                        .cache(postgresRawStorageRepository)
                        .build())
                .cache(postgresRawStorageRepository)
                .build();
    }

    @Bean
    RawStorageReader rawStorageReader(
            final GithubRawStorageReader githubRawStorageReader,
            final PostgresRawStorageWriter postgresRawStorageRepository
    ) {
        return CacheWriteRawStorageReaderDecorator.builder()
                .fetcher(githubRawStorageReader)
                .cache(postgresRawStorageRepository)
                .build();
    }

    @Bean
    public InstallationEventProcessorService eventProcessorService(final PostgresRawInstallationEventStorageStorage postgresRawInstallationEventStorageRepository,
                                                                   final RawStorageReader cachedRawStorageReader,
                                                                   final PostgresGithubRepoStorage postgresGithubRepoRepository,
                                                                   final PostgresRepoIndexingJobStorage repoIndexingJobRepository,
                                                                   final PostgresOldRepoIndexingJobStorage oldRepoIndexesEntityRepository,
                                                                   final UserIndexer cachedUserIndexer,
                                                                   final RepoIndexer cachedRepoIndexer,
                                                                   final GithubAppInstallationStorage githubAppInstallationStorage) {
        return new InstallationEventProcessorService(postgresRawInstallationEventStorageRepository,
                cachedRawStorageReader,
                postgresGithubRepoRepository,
                new RepoIndexingJobStorageComposite(repoIndexingJobRepository, oldRepoIndexesEntityRepository),
                cachedUserIndexer,
                cachedRepoIndexer,
                githubAppInstallationStorage);
    }

    @Bean
    GithubRawStorageReader githubRawStorageReader(final GithubHttpClient githubHttpClient) {
        return new GithubRawStorageReader(githubHttpClient);
    }

    @Bean
    public UserIndexer cachedUserIndexer(final RawStorageReader cachedRawStorageReader, final MeterRegistry registry) {
        return new MonitoredUserIndexer(new UserIndexingService(cachedRawStorageReader), registry);
    }

    @Bean
    public IssueIndexer cachedIssueIndexer(final RawStorageReader cachedRawStorageReader,
                                           final UserIndexer cachedUserIndexer,
                                           final RepoIndexer cachedRepoIndexer,
                                           final ContributionStorage contributionStorage,
                                           final MeterRegistry registry) {
        return new IssueContributionExposer(
                new MonitoredIssueIndexer(
                        new IssueIndexingService(cachedRawStorageReader, cachedUserIndexer, cachedRepoIndexer),
                        registry
                ),
                contributionStorage
        );
    }


    @Bean
    public PullRequestIndexer cachedPullRequestIndexer(
            final RawStorageReader cachedRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final RepoIndexer cachedRepoIndexer,
            final IssueIndexer cachedIssueIndexer,
            final ContributionStorage contributionStorage,
            final MeterRegistry registry) {
        return new PullRequestContributionExposer(
                new MonitoredPullRequestIndexer(
                        new PullRequestIndexingService(cachedRawStorageReader, cachedUserIndexer, cachedRepoIndexer, cachedIssueIndexer),
                        registry),
                contributionStorage
        );
    }

    @Bean
    public RepoIndexer cachedRepoIndexer(
            final RawStorageReader cachedRawStorageReader,
            final UserIndexer cachedUserIndexer) {
        return new RepoIndexingService(cachedRawStorageReader, cachedUserIndexer);
    }

    @Bean
    public FullRepoIndexer cachedFullRepoIndexer(
            final RawStorageReader cachedRawStorageReader,
            final IssueIndexer cachedIssueIndexer,
            final PullRequestIndexer cachedPullRequestIndexer,
            final RepoIndexer cachedRepoIndexer) {
        return new FullRepoIndexingService(cachedRawStorageReader, cachedIssueIndexer, cachedPullRequestIndexer, cachedRepoIndexer);
    }

    @Bean
    public FullRepoIndexer refreshingFullRepoIndexer(
            final RawStorageReader rawStorageReader,
            final IssueIndexer cachedIssueIndexer,
            final PullRequestIndexer cachedPullRequestIndexer,
            final RepoIndexer cachedRepoIndexer,
            final MeterRegistry registry,
            final GithubRateLimitServiceAdapter rateLimitService,
            final GithubRateLimitServiceAdapter.Config githubrateLimitConfig) {
        return new RateLimitGuardedFullRepoIndexer(
                new MonitoredFullRepoIndexer(
                        new FullRepoIndexingService(rawStorageReader, cachedIssueIndexer, cachedPullRequestIndexer, cachedRepoIndexer),
                        registry
                ),
                rateLimitService,
                githubrateLimitConfig,
                registry
        );
    }

    @Bean
    public RepoRefreshJobManager repoRefreshJobScheduler(
            final PostgresRepoIndexingJobStorage repoIndexingJobTriggerRepository,
            final FullRepoIndexer refreshingFullRepoIndexer,
            final GithubAppContext githubAppContext
    ) {
        return new RepoRefreshJobService(repoIndexingJobTriggerRepository, refreshingFullRepoIndexer, githubAppContext);
    }

    @Bean
    public RepoRefreshJobManager repoCacheRefreshJobScheduler(
            final PostgresRepoIndexingJobStorage repoIndexingJobTriggerRepository,
            final FullRepoIndexer cachedFullRepoIndexer,
            final GithubAppContext githubAppContext
    ) {
        return new RepoRefreshJobService(repoIndexingJobTriggerRepository, cachedFullRepoIndexer, githubAppContext);
    }

    @Bean
    public UserRefreshJobManager userRefreshJobScheduler(
            final PostgresUserIndexingJobStorage userIndexingJobTriggerRepository,
            final UserIndexer refreshingUserIndexer
    ) {
        return new UserRefreshJobService(userIndexingJobTriggerRepository, refreshingUserIndexer);
    }

    @Bean
    public UserRefreshJobManager userCacheRefreshJobScheduler(
            final PostgresUserIndexingJobStorage userIndexingJobTriggerRepository,
            final UserIndexer cachedUserIndexer
    ) {
        return new UserRefreshJobService(userIndexingJobTriggerRepository, cachedUserIndexer);
    }
}
