package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.events.InstallationEventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.events.RepositoryEventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.out.ApiClient;
import com.onlydust.marketplace.indexer.domain.ports.out.RateLimitService;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.*;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.NotifierJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorageComposite;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.CacheReadRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.CacheWriteRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.DiffRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.services.events.InstallationEventProcessorService;
import com.onlydust.marketplace.indexer.domain.services.events.RepositoryEventProcessorService;
import com.onlydust.marketplace.indexer.domain.services.exposers.*;
import com.onlydust.marketplace.indexer.domain.services.guards.RateLimitGuardedFullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.services.indexers.*;
import com.onlydust.marketplace.indexer.domain.services.jobs.*;
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
import org.springframework.core.task.TaskExecutor;

@Configuration
public class DomainConfiguration {
    @Bean
    @ConfigurationProperties("infrastructure.github")
    GithubConfig githubConfig() {
        return new GithubConfig();
    }

    @Bean
    @ConfigurationProperties("infrastructure.github-for-app")
    GithubConfig githubConfigForApp() {
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
    @ConfigurationProperties("application.repo-refresh-job")
    RepoRefreshJobService.Config repoRefreshJobConfig() {
        return new RepoRefreshJobService.Config();
    }

    @Bean
    @ConfigurationProperties("application.user-refresh-job")
    UserRefreshJobService.Config userRefreshJobConfig() {
        return new UserRefreshJobService.Config();
    }


    @Bean
    GithubRateLimitServiceAdapter githubRateLimitServiceAdapter(final GithubHttpClient githubHttpClient) {
        return new GithubRateLimitServiceAdapter(githubHttpClient);
    }

    @Bean
    RawStorageReader liveRawStorageReader(
            final RawStorageReader githubRawStorageReader,
            final PostgresRawStorage postgresRawStorage
    ) {
        return CacheWriteRawStorageReaderDecorator.builder()
                .fetcher(githubRawStorageReader)
                .cache(postgresRawStorage)
                .build();
    }

    @Bean
    RawStorageReader cachedRawStorageReader(
            final RawStorageReader liveRawStorageReader,
            final PostgresRawStorage postgresRawStorage
    ) {
        return CacheReadRawStorageReaderDecorator.builder()
                .fetcher(liveRawStorageReader)
                .cache(postgresRawStorage)
                .build();
    }

    @Bean
    RawStorageReader diffRawStorageReader(
            final RawStorageReader liveRawStorageReader,
            final PostgresRawStorage postgresRawStorage
    ) {
        return DiffRawStorageReaderDecorator.builder()
                .fetcher(liveRawStorageReader)
                .cache(postgresRawStorage)
                .build();
    }

    @Bean
    public InstallationEventHandler installationEventHandler(final PostgresRawInstallationEventStorageStorage postgresRawInstallationEventStorageRepository,
                                                             final PostgresRepoIndexingJobStorage repoIndexingJobRepository,
                                                             final PostgresOldRepoIndexingJobStorage oldRepoIndexesEntityRepository,
                                                             final GithubAppInstallationStorage githubAppInstallationStorage) {
        return new InstallationEventProcessorService(
                postgresRawInstallationEventStorageRepository,
                new RepoIndexingJobStorageComposite(repoIndexingJobRepository, oldRepoIndexesEntityRepository),
                githubAppInstallationStorage);
    }

    @Bean
    public RepositoryEventHandler repositoryEventHandler(final PostgresRepoIndexingJobStorage repoIndexingJobRepository,
                                                         final PostgresOldRepoIndexingJobStorage oldRepoIndexesEntityRepository,
                                                         final RepoStorage repoStorage) {
        return new RepositoryEventProcessorService(
                new RepoIndexingJobStorageComposite(repoIndexingJobRepository, oldRepoIndexesEntityRepository),
                repoStorage);
    }

    @Bean
    RawStorageReader githubRawStorageReader(final GithubHttpClient githubHttpClient) {
        return new GithubRawStorageReader(githubHttpClient);
    }

    @Bean
    public UserIndexer cachedUserIndexer(final RawStorageReader cachedRawStorageReader, final MeterRegistry registry) {
        return new MonitoredUserIndexer(new UserIndexingService(cachedRawStorageReader), registry);
    }

    @Bean
    public UserIndexer cacheOnlyUserIndexer(final PostgresRawStorage postgresRawStorage) {
        return new UserIndexingService(postgresRawStorage);
    }

    @Bean
    public UserIndexer diffUserIndexer(final RawStorageReader diffRawStorageReader,
                                       final MeterRegistry registry,
                                       final AccountStorage accountStorage
    ) {
        return new MonitoredUserIndexer(
                new UserExposer(
                        new UserIndexingService(diffRawStorageReader), accountStorage),
                registry);
    }

    @Bean
    public IssueIndexer cachedIssueIndexer(final RawStorageReader cachedRawStorageReader,
                                           final UserIndexer cachedUserIndexer,
                                           final RepoIndexer cachedRepoIndexer,
                                           final ContributionStorage contributionStorage,
                                           final IssueStorage issueStorage,
                                           final MeterRegistry registry) {
        return new IssueExposer(
                new MonitoredIssueIndexer(
                        new IssueIndexingService(cachedRawStorageReader, cachedUserIndexer, cachedRepoIndexer),
                        registry
                ),
                contributionStorage, issueStorage
        );
    }

    @Bean
    public IssueIndexer cacheOnlyIssueIndexer(final PostgresRawStorage postgresRawStorage,
                                              final UserIndexer cacheOnlyUserIndexer,
                                              final RepoIndexer cacheOnlyRepoIndexer,
                                              final ContributionStorage contributionStorage,
                                              final IssueStorage issueStorage) {
        return new IssueExposer(
                new IssueIndexingService(postgresRawStorage, cacheOnlyUserIndexer, cacheOnlyRepoIndexer),
                contributionStorage, issueStorage
        );
    }

    @Bean
    public IssueIndexer liveIssueIndexer(final RawStorageReader liveRawStorageReader,
                                         final UserIndexer cachedUserIndexer,
                                         final RepoIndexer cachedRepoIndexer,
                                         final ContributionStorage contributionStorage,
                                         final IssueStorage issueStorage,
                                         final MeterRegistry registry) {
        return new IssueExposer(
                new MonitoredIssueIndexer(
                        new IssueIndexingService(liveRawStorageReader, cachedUserIndexer, cachedRepoIndexer),
                        registry
                ),
                contributionStorage, issueStorage
        );
    }

    @Bean
    public PullRequestIndexer cachedPullRequestIndexer(
            final RawStorageReader cachedRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final RepoIndexer cachedRepoIndexer,
            final IssueIndexer cachedIssueIndexer,
            final ContributionStorage contributionStorage,
            final PullRequestStorage pullRequestStorage,
            final MeterRegistry registry) {
        return new PullRequestExposer(
                new MonitoredPullRequestIndexer(
                        new PullRequestIndexingService(cachedRawStorageReader, cachedUserIndexer, cachedRepoIndexer, cachedIssueIndexer),
                        registry),
                contributionStorage,
                pullRequestStorage
        );
    }


    @Bean
    public PullRequestIndexer cacheOnlyPullRequestIndexer(
            final PostgresRawStorage postgresRawStorage,
            final UserIndexer cacheOnlyUserIndexer,
            final RepoIndexer cacheOnlyRepoIndexer,
            final IssueIndexer cacheOnlyIssueIndexer,
            final ContributionStorage contributionStorage,
            final PullRequestStorage pullRequestStorage) {
        return new PullRequestExposer(
                new PullRequestIndexingService(postgresRawStorage, cacheOnlyUserIndexer, cacheOnlyRepoIndexer, cacheOnlyIssueIndexer),
                contributionStorage,
                pullRequestStorage
        );
    }


    @Bean
    public PullRequestIndexer livePullRequestIndexer(
            final RawStorageReader liveRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final RepoIndexer cachedRepoIndexer,
            final IssueIndexer cachedIssueIndexer,
            final ContributionStorage contributionStorage,
            final PullRequestStorage pullRequestStorage,
            final MeterRegistry registry) {
        return new PullRequestExposer(
                new MonitoredPullRequestIndexer(
                        new PullRequestIndexingService(liveRawStorageReader, cachedUserIndexer, cachedRepoIndexer, cachedIssueIndexer),
                        registry),
                contributionStorage,
                pullRequestStorage
        );
    }


    @Bean
    public RepoIndexer cachedRepoIndexer(
            final RawStorageReader cachedRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final RepoStorage postgresRepoStorage) {
        return new RepoExposer(new RepoIndexingService(cachedRawStorageReader, cachedUserIndexer), postgresRepoStorage);
    }

    @Bean
    public RepoIndexer cacheOnlyRepoIndexer(
            final PostgresRawStorage postgresRawStorage,
            final UserIndexer cacheOnlyUserIndexer,
            final RepoStorage postgresRepoStorage) {
        return new RepoExposer(new RepoIndexingService(postgresRawStorage, cacheOnlyUserIndexer), postgresRepoStorage);
    }


    @Bean
    public RepoIndexer liveRepoIndexer(
            final RawStorageReader liveRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final RepoStorage postgresRepoStorage) {
        return new RepoExposer(new RepoIndexingService(liveRawStorageReader, cachedUserIndexer), postgresRepoStorage);
    }


    @Bean
    public RepoIndexer cachedFullRepoIndexer(
            final RawStorageReader cachedRawStorageReader,
            final IssueIndexer cachedIssueIndexer,
            final PullRequestIndexer cachedPullRequestIndexer,
            final RepoIndexer cachedRepoIndexer,
            final RepoContributorsStorage repoContributorsStorage) {
        return new RepoContributorsExposer(
                new FullRepoIndexingService(cachedRawStorageReader, cachedIssueIndexer, cachedPullRequestIndexer, cachedRepoIndexer),
                repoContributorsStorage
        );
    }

    @Bean
    public RepoIndexer cacheOnlyFullRepoIndexer(
            final PostgresRawStorage postgresRawStorage,
            final IssueIndexer cacheOnlyIssueIndexer,
            final PullRequestIndexer cacheOnlyPullRequestIndexer,
            final RepoIndexer cacheOnlyRepoIndexer,
            final RepoContributorsStorage repoContributorsStorage) {
        return new RepoContributorsExposer(
                new FullRepoIndexingService(postgresRawStorage, cacheOnlyIssueIndexer, cacheOnlyPullRequestIndexer, cacheOnlyRepoIndexer),
                repoContributorsStorage
        );
    }

    @Bean
    public RepoIndexer diffFullRepoIndexer(
            final RawStorageReader diffRawStorageReader,
            final IssueIndexer liveIssueIndexer,
            final PullRequestIndexer livePullRequestIndexer,
            final RepoIndexer liveRepoIndexer,
            final RepoContributorsStorage repoContributorsStorage,
            final RateLimitService rateLimitService,
            final RateLimitService.Config rateLimitConfig,
            final MeterRegistry registry,
            final GithubAppContext githubAppContext
    ) {
        return new RepoContributorsExposer(
                new RateLimitGuardedFullRepoIndexer(
                        new FullRepoIndexingService(diffRawStorageReader, liveIssueIndexer, livePullRequestIndexer, liveRepoIndexer),
                        rateLimitService, rateLimitConfig, registry, githubAppContext),
                repoContributorsStorage
        );
    }

    @Bean
    public JobManager diffRepoRefreshJobManager(
            final TaskExecutor applicationTaskExecutor,
            final PostgresRepoIndexingJobStorage repoIndexingJobTriggerRepository,
            final RepoIndexer diffFullRepoIndexer,
            final RepoIndexer liveRepoIndexer,
            final GithubAppContext githubAppContext,
            final RepoRefreshJobService.Config repoRefreshJobConfig
    ) {
        return new RepoRefreshJobService(applicationTaskExecutor, repoIndexingJobTriggerRepository, diffFullRepoIndexer, liveRepoIndexer, githubAppContext, repoRefreshJobConfig);
    }

    @Bean
    public JobManager cacheOnlyRepoRefreshJobManager(
            final TaskExecutor applicationTaskExecutor,
            final PostgresRepoIndexingJobStorage repoIndexingJobTriggerRepository,
            final RepoIndexer cacheOnlyFullRepoIndexer,
            final RepoIndexer cacheOnlyRepoIndexer,
            final GithubAppContext githubAppContext,
            final RepoRefreshJobService.Config repoRefreshJobConfig
    ) {
        return new RepoRefreshJobService(applicationTaskExecutor, repoIndexingJobTriggerRepository, cacheOnlyFullRepoIndexer, cacheOnlyRepoIndexer, githubAppContext, repoRefreshJobConfig);
    }

    @Bean
    public JobManager diffUserRefreshJobManager(
            final PostgresUserIndexingJobStorage userIndexingJobTriggerRepository,
            final UserIndexer diffUserIndexer,
            final UserRefreshJobService.Config userRefreshJobConfig
    ) {
        return new UserRefreshJobService(userIndexingJobTriggerRepository, diffUserIndexer, userRefreshJobConfig);
    }

    @Bean
    public JobManager cachedUserRefreshJobManager(
            final PostgresUserIndexingJobStorage userIndexingJobTriggerRepository,
            final UserIndexer cachedUserIndexer,
            final UserRefreshJobService.Config userRefreshJobConfig
    ) {
        return new UserRefreshJobService(userIndexingJobTriggerRepository, cachedUserIndexer, userRefreshJobConfig);
    }

    @Bean
    public JobManager apiNotifier(final ContributionStorage contributionStorage, final ApiClient apiClient, final NotifierJobStorage notifierJobStorage) {
        return new NotifierJobManagerJobService(contributionStorage, apiClient, notifierJobStorage);
    }

    @Bean
    public RepoIndexingJobScheduler repoIndexingJobScheduler(final PostgresRepoIndexingJobStorage repoIndexingJobStorage) {
        return new RepoIndexingJobSchedulerService(repoIndexingJobStorage);
    }


    @Bean
    public UserIndexingJobScheduler userIndexingJobScheduler(final PostgresUserIndexingJobStorage userIndexingJobStorage) {
        return new UserIndexingJobSchedulerService(userIndexingJobStorage);
    }
}
