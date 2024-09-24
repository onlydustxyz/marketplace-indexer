package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawStarEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.*;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventsInbox;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.*;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserStatsIndexingJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.IndexingObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.RateLimitService;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.*;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserStatsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.*;
import com.onlydust.marketplace.indexer.domain.services.events.*;
import com.onlydust.marketplace.indexer.domain.services.exposers.*;
import com.onlydust.marketplace.indexer.domain.services.guards.RateLimitGuardedFullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.services.indexers.*;
import com.onlydust.marketplace.indexer.domain.services.jobs.*;
import com.onlydust.marketplace.indexer.domain.services.monitoring.MonitoredIssueIndexer;
import com.onlydust.marketplace.indexer.domain.services.monitoring.MonitoredPullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.services.monitoring.MonitoredUserIndexer;
import com.onlydust.marketplace.indexer.domain.services.observers.GithubOutboxObserver;
import com.onlydust.marketplace.indexer.domain.services.observers.IndexingOutboxObserver;
import com.onlydust.marketplace.indexer.github.GithubConfig;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import com.onlydust.marketplace.indexer.github.adapters.GithubRateLimitServiceAdapter;
import com.onlydust.marketplace.indexer.github.adapters.GithubRawStorageReader;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRawStorage;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresUserIndexingJobStorage;
import io.micrometer.core.instrument.MeterRegistry;
import onlydust.com.marketplace.kernel.infrastructure.github.GithubAppJwtBuilder;
import onlydust.com.marketplace.kernel.port.output.OutboxPort;
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
    GithubAppJwtBuilder.Config githubAppConfig() {
        return new GithubAppJwtBuilder.Config();
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
    @ConfigurationProperties("application.user-stats-job")
    UserStatsIndexingJobService.Config userStatsJobConfig() {
        return new UserStatsIndexingJobService.Config();
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
    PublicEventRawStorageReader livePublicEventRawStorageReader(
            final PublicEventRawStorageReader githubPublicEventRawStorageReader,
            final PostgresRawStorage postgresRawStorage
    ) {
        return CacheWritePublicEventRawStorageReaderDecorator.builder()
                .fetcher(githubPublicEventRawStorageReader)
                .cache(postgresRawStorage)
                .build();
    }

    @Bean
    public EventHandler<RawInstallationEvent> installationEventHandler(final PostgresRepoIndexingJobStorage repoIndexingJobRepository,
                                                                       final GithubAppInstallationStorage githubAppInstallationStorage) {
        return new InstallationEventProcessorService(
                repoIndexingJobRepository,
                githubAppInstallationStorage);
    }

    @Bean
    public EventHandler<RawRepositoryEvent> repositoryEventHandler(final PostgresRepoIndexingJobStorage repoIndexingJobRepository,
                                                                   final RepoStorage repoStorage,
                                                                   final RawStorageWriter rawStorageWriter,
                                                                   final RepoIndexer liveRepoIndexer) {
        return new RepositoryEventProcessorService(
                repoIndexingJobRepository,
                repoStorage, rawStorageWriter, liveRepoIndexer);
    }

    @Bean
    public EventHandler<RawStarEvent> starEventHandler(final RepoIndexer liveRepoIndexer) {
        return new StarEventProcessorService(liveRepoIndexer);
    }

    @Bean
    public EventHandler<RawIssueEvent> issueEventHandler(final IssueIndexer liveIssueIndexer,
                                                         final Exposer<CleanRepo> repoContributorsExposer,
                                                         final GithubAppContext githubAppContext,
                                                         final RawStorageWriter rawStorageWriter,
                                                         final IssueStorage issueStorage,
                                                         final ContributionStorage contributionStorage,
                                                         final GithubObserver githubObserver) {
        return new IssueEventProcessorService(
                liveIssueIndexer,
                repoContributorsExposer,
                githubAppContext,
                rawStorageWriter,
                issueStorage,
                contributionStorage,
                githubObserver);
    }

    @Bean
    public EventHandler<RawIssueCommentEvent> issueCommentEventHandler(final GithubObserver githubObserver) {
        return new IssueCommentEventProcessorService(githubObserver);
    }

    @Bean
    public EventHandler<RawPullRequestEvent> pullRequestEventHandler(final Exposer<CleanRepo> repoContributorsExposer,
                                                                     final PullRequestIndexer livePullRequestIndexer,
                                                                     final GithubAppContext githubAppContext,
                                                                     final GithubObserver githubObserver) {
        return new PullRequestEventProcessorService(
                repoContributorsExposer,
                livePullRequestIndexer,
                githubAppContext,
                githubObserver);
    }

    @Bean
    RawStorageReader githubRawStorageReader(final GithubHttpClient githubHttpClient) {
        return new GithubRawStorageReader(githubHttpClient);
    }

    @Bean
    PublicEventRawStorageReader githubPublicEventRawStorageReader(final GithubHttpClient githubHttpClient) {
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
                                       final Exposer<CleanAccount> userExposer
    ) {
        return new MonitoredUserIndexer(
                new UserExposerIndexer(
                        new UserIndexingService(diffRawStorageReader), userExposer),
                registry);
    }

    @Bean
    public UserStatsIndexer cachedUserStatsIndexer(final PublicEventRawStorageReader livePublicEventRawStorageReader) {
        return new UserStatsIndexingService(livePublicEventRawStorageReader);
    }

    @Bean
    public IssueIndexer cachedIssueIndexer(final RawStorageReader cachedRawStorageReader,
                                           final UserIndexer cachedUserIndexer,
                                           final RepoIndexer cachedRepoIndexer,
                                           final Exposer<CleanIssue> issueExposer,
                                           final MeterRegistry registry) {
        return new IssueExposerIndexer(
                new MonitoredIssueIndexer(
                        new IssueIndexingService(cachedRawStorageReader, cachedUserIndexer, cachedRepoIndexer),
                        registry
                ),
                issueExposer
        );
    }

    @Bean
    public IssueIndexer cacheOnlyIssueIndexer(final PostgresRawStorage postgresRawStorage,
                                              final UserIndexer cacheOnlyUserIndexer,
                                              final RepoIndexer cacheOnlyRepoIndexer,
                                              final Exposer<CleanIssue> issueExposer) {
        return new IssueExposerIndexer(
                new IssueIndexingService(postgresRawStorage, cacheOnlyUserIndexer, cacheOnlyRepoIndexer),
                issueExposer
        );
    }

    @Bean
    public IssueIndexer liveIssueIndexer(final RawStorageReader liveRawStorageReader,
                                         final UserIndexer cachedUserIndexer,
                                         final RepoIndexer cachedRepoIndexer,
                                         final Exposer<CleanIssue> issueExposer,
                                         final MeterRegistry registry) {
        return new IssueExposerIndexer(
                new MonitoredIssueIndexer(
                        new IssueIndexingService(liveRawStorageReader, cachedUserIndexer, cachedRepoIndexer),
                        registry
                ),
                issueExposer
        );
    }

    @Bean
    public PullRequestIndexer cachedPullRequestIndexer(
            final RawStorageReader cachedRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final RepoIndexer cachedRepoIndexer,
            final IssueIndexer cachedIssueIndexer,
            final Exposer<CleanPullRequest> pullRequestExposer,
            final MeterRegistry registry) {
        return new PullRequestExposerIndexer(
                new MonitoredPullRequestIndexer(
                        new PullRequestIndexingService(cachedRawStorageReader, cachedUserIndexer, cachedRepoIndexer, cachedIssueIndexer),
                        registry),
                pullRequestExposer
        );
    }

    @Bean
    public PullRequestIndexer cacheOnlyPullRequestIndexer(
            final PostgresRawStorage postgresRawStorage,
            final UserIndexer cacheOnlyUserIndexer,
            final RepoIndexer cacheOnlyRepoIndexer,
            final IssueIndexer cacheOnlyIssueIndexer,
            final Exposer<CleanPullRequest> pullRequestExposer) {
        return new PullRequestExposerIndexer(
                new PullRequestIndexingService(postgresRawStorage, cacheOnlyUserIndexer, cacheOnlyRepoIndexer, cacheOnlyIssueIndexer),
                pullRequestExposer
        );
    }

    @Bean
    public PullRequestIndexer livePullRequestIndexer(
            final RawStorageReader liveRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final RepoIndexer cachedRepoIndexer,
            final IssueIndexer cachedIssueIndexer,
            final Exposer<CleanPullRequest> pullRequestExposer,
            final MeterRegistry registry) {
        return new PullRequestExposerIndexer(
                new MonitoredPullRequestIndexer(
                        new PullRequestIndexingService(liveRawStorageReader, cachedUserIndexer, cachedRepoIndexer, cachedIssueIndexer),
                        registry),
                pullRequestExposer
        );
    }

    @Bean
    public RepoIndexer cachedRepoIndexer(
            final RawStorageReader cachedRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final Exposer<CleanRepo> repoExposer) {
        return new RepoExposerIndexer(new RepoIndexingService(cachedRawStorageReader, cachedUserIndexer), repoExposer);
    }

    @Bean
    public RepoIndexer cacheOnlyRepoIndexer(
            final PostgresRawStorage postgresRawStorage,
            final UserIndexer cacheOnlyUserIndexer,
            final Exposer<CleanRepo> repoExposer) {
        return new RepoExposerIndexer(new RepoIndexingService(postgresRawStorage, cacheOnlyUserIndexer), repoExposer);
    }


    @Bean
    public RepoIndexer liveRepoIndexer(
            final RawStorageReader liveRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final Exposer<CleanRepo> repoExposer) {
        return new RepoExposerIndexer(new RepoIndexingService(liveRawStorageReader, cachedUserIndexer), repoExposer);
    }

    @Bean
    public RepoIndexer cachedFullRepoIndexer(
            final RawStorageReader cachedRawStorageReader,
            final IssueIndexer cachedIssueIndexer,
            final PullRequestIndexer cachedPullRequestIndexer,
            final RepoIndexer cachedRepoIndexer,
            final Exposer<CleanRepo> repoContributorsExposer) {
        return new RepoExposerIndexer(
                new FullRepoIndexingService(cachedRawStorageReader, cachedIssueIndexer, cachedPullRequestIndexer, cachedRepoIndexer),
                repoContributorsExposer
        );
    }

    @Bean
    public RepoIndexer cacheOnlyFullRepoIndexer(
            final PostgresRawStorage postgresRawStorage,
            final IssueIndexer cacheOnlyIssueIndexer,
            final PullRequestIndexer cacheOnlyPullRequestIndexer,
            final RepoIndexer cacheOnlyRepoIndexer,
            final Exposer<CleanRepo> repoContributorsExposer) {
        return new RepoExposerIndexer(
                new FullRepoIndexingService(postgresRawStorage, cacheOnlyIssueIndexer, cacheOnlyPullRequestIndexer, cacheOnlyRepoIndexer),
                repoContributorsExposer
        );
    }

    @Bean
    public RepoIndexer diffFullRepoIndexer(
            final RawStorageReader diffRawStorageReader,
            final IssueIndexer liveIssueIndexer,
            final PullRequestIndexer livePullRequestIndexer,
            final RepoIndexer liveRepoIndexer,
            final Exposer<CleanRepo> repoContributorsExposer,
            final RateLimitService rateLimitService,
            final RateLimitService.Config rateLimitConfig,
            final MeterRegistry registry,
            final GithubAppContext githubAppContext
    ) {
        return new RepoExposerIndexer(
                new RateLimitGuardedFullRepoIndexer(
                        new FullRepoIndexingService(diffRawStorageReader, liveIssueIndexer, livePullRequestIndexer, liveRepoIndexer),
                        rateLimitService, rateLimitConfig, registry, githubAppContext),
                repoContributorsExposer
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
        return new RepoRefreshJobService(applicationTaskExecutor, repoIndexingJobTriggerRepository, diffFullRepoIndexer, liveRepoIndexer, githubAppContext,
                repoRefreshJobConfig);
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
        return new RepoRefreshJobService(applicationTaskExecutor, repoIndexingJobTriggerRepository, cacheOnlyFullRepoIndexer, cacheOnlyRepoIndexer,
                githubAppContext, repoRefreshJobConfig);
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
    public RepoIndexingJobScheduler repoIndexingJobScheduler(final PostgresRepoIndexingJobStorage repoIndexingJobStorage) {
        return new RepoIndexingJobSchedulerService(repoIndexingJobStorage);
    }


    @Bean
    public UserIndexingJobScheduler userIndexingJobScheduler(final PostgresUserIndexingJobStorage userIndexingJobStorage) {
        return new UserIndexingJobSchedulerService(userIndexingJobStorage);
    }

    @Bean
    public UserStatsIndexingJobManager userStatsIndexingJobManager(
            final UserStatsIndexingJobStorage userStatsIndexingJobStorage,
            final UserStatsIndexer userStatsIndexer,
            final RawStorageReader cachedRawStorageReader,
            final UserStatsIndexingJobService.Config userStatsJobConfig
    ) {
        return new UserStatsIndexingJobService(userStatsIndexingJobStorage, userStatsIndexer, cachedRawStorageReader, userStatsJobConfig);
    }

    @Bean
    public EventsInbox eventsInbox(final EventInboxStorage eventInboxStorage) {
        return new EventInboxService(eventInboxStorage);
    }

    @Bean
    public Exposer<CleanAccount> userExposer(final AccountStorage accountStorage) {
        return new UserExposer(accountStorage);
    }

    @Bean
    public Exposer<CleanIssue> issueExposer(final ContributionStorage contributionStorage,
                                            final IssueStorage issueStorage,
                                            final IndexingObserver indexingOutboxObserver) {
        return new IssueExposer(contributionStorage, issueStorage, indexingOutboxObserver);
    }

    @Bean
    public Exposer<CleanPullRequest> pullRequestExposer(final ContributionStorage contributionStorage,
                                                        final PullRequestStorage pullRequestStorage,
                                                        final IndexingObserver indexingOutboxObserver) {
        return new PullRequestExposer(contributionStorage, pullRequestStorage, indexingOutboxObserver);
    }

    @Bean
    public Exposer<CleanRepo> repoExposer(final RepoStorage postgresRepoStorage) {
        return new RepoExposer(postgresRepoStorage);
    }

    @Bean
    public Exposer<CleanRepo> repoContributorsExposer(final RepoContributorsStorage repoContributorsStorage) {
        return new RepoContributorsExposer(repoContributorsStorage);
    }

    @Bean
    public IndexingOutboxObserver indexingOutboxObserver(final OutboxPort outboxPort) {
        return new IndexingOutboxObserver(outboxPort);
    }

    @Bean
    public GithubOutboxObserver githubOutboxObserver(final OutboxPort outboxPort) {
        return new GithubOutboxObserver(outboxPort);
    }
}
