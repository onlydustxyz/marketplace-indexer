package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.models.clean.*;
import com.onlydust.marketplace.indexer.domain.models.raw.RawStarEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.*;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventsInbox;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.*;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserPublicEventsIndexingJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.IndexingObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.RateLimitService;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.*;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.CommitIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
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
import com.onlydust.marketplace.indexer.infrastructure.aws_athena.AwsAthenaClient;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresCommitIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRawStorage;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresUserIndexingJobStorage;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import onlydust.com.marketplace.kernel.port.output.OutboxPort;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

@Slf4j
@Configuration
public class DomainConfiguration {
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
            final AwsAthenaClient.Properties awsAthenaProperties,
            final PublicEventRawStorageReader awsAthenaPublicEventRawStorageReaderAdapter
    ) {
        if (awsAthenaProperties.getDatabase() == null)
            return new PublicEventRawStorageReader() {
                @Override
                public Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since) {
                    return Stream.empty();
                }

                @Override
                public Stream<RawPublicEvent> allPublicEvents(ZonedDateTime since) {
                    return Stream.empty();
                }
            };
        return awsAthenaPublicEventRawStorageReaderAdapter;
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
                                                                   final RepoIndexer cachedRepoIndexer) {
        return new RepositoryEventProcessorService(
                repoIndexingJobRepository,
                repoStorage, rawStorageWriter, cachedRepoIndexer);
    }

    @Bean
    public EventHandler<RawStarEvent> starEventHandler(final RepoIndexer liveRepoIndexer) {
        return new StarEventProcessorService(liveRepoIndexer);
    }

    @Bean
    public EventHandler<RawIssueEvent> issueEventHandler(final IssueIndexer cachedIssueIndexer,
                                                         final UserIndexer cachedUserIndexer,
                                                         final Exposer<CleanRepo> repoContributorsExposer,
                                                         final GithubAppContext githubAppContext,
                                                         final RawStorageWriter rawStorageWriter,
                                                         final IssueStorage issueStorage,
                                                         final ContributionStorage contributionStorage,
                                                         final GithubObserver githubObserver,
                                                         final IndexingObserver indexingObserver) {
        return new IssueEventProcessorService(
                cachedIssueIndexer,
                cachedUserIndexer,
                repoContributorsExposer,
                githubAppContext,
                rawStorageWriter,
                issueStorage,
                contributionStorage,
                githubObserver,
                indexingObserver);
    }

    @Bean
    public EventHandler<RawIssueCommentEvent> issueCommentEventHandler(final GithubObserver githubObserver) {
        return new IssueCommentEventProcessorService(githubObserver);
    }

    @Bean
    public EventHandler<RawPullRequestEvent> pullRequestEventHandler(final Exposer<CleanRepo> repoContributorsExposer,
                                                                     final RawStorageWriter rawStorageWriter,
                                                                     final PullRequestIndexer cachedPullRequestIndexer,
                                                                     final GithubAppContext githubAppContext,
                                                                     final GithubObserver githubObserver) {
        return new PullRequestEventProcessorService(
                repoContributorsExposer,
                rawStorageWriter,
                cachedPullRequestIndexer,
                githubAppContext,
                githubObserver);
    }

    @Bean
    public EventHandler<RawPullRequestReviewEvent> pullRequestReviewEventHandler(final Exposer<CleanRepo> repoContributorsExposer,
                                                                                 final RawStorageReader postgresRawStorageRepository,
                                                                                 final RawStorageWriter rawStorageWriter,
                                                                                 final PullRequestIndexer cachedPullRequestIndexer,
                                                                                 final GithubAppContext githubAppContext) {
        return new PullRequestReviewEventProcessorService(
                repoContributorsExposer,
                postgresRawStorageRepository,
                rawStorageWriter,
                cachedPullRequestIndexer,
                githubAppContext);
    }

    @Bean
    public UserIndexer cachedUserIndexer(final RawStorageReader cachedRawStorageReader,
                                         final MeterRegistry registry,
                                         final Exposer<CleanAccount> userExposer) {
        return new MonitoredUserIndexer(
                new UserExposerIndexer(
                        new UserIndexingService(cachedRawStorageReader), userExposer),
                registry);
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
    public UserPublicEventsIndexer cachedUserPublicEventsIndexer(final PublicEventRawStorageReader livePublicEventRawStorageReader,
                                                                 final UserPublicEventsIndexingJobStorage userPublicEventsIndexingJobStorage,
                                                                 final RawStorageWriter rawStorageWriter,
                                                                 final RawStorageReader cachedRawStorageReader,
                                                                 final RepoIndexer cacheOnlyRepoIndexer,
                                                                 final UserIndexer cacheOnlyUserIndexer,
                                                                 final PullRequestIndexer cacheOnlyPullRequestIndexer,
                                                                 final IssueIndexer cacheOnlyIssueIndexer
    ) {
        return new UserPublicEventsIndexingService(livePublicEventRawStorageReader,
                userPublicEventsIndexingJobStorage,
                rawStorageWriter,
                cachedRawStorageReader,
                cacheOnlyRepoIndexer,
                cacheOnlyUserIndexer,
                cacheOnlyPullRequestIndexer,
                cacheOnlyIssueIndexer);
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
            final CommitIndexer cachedCommitIndexer,
            final Exposer<CleanPullRequest> pullRequestExposer,
            final MeterRegistry registry) {
        return new PullRequestExposerIndexer(
                new MonitoredPullRequestIndexer(
                        new PullRequestIndexingService(cachedRawStorageReader, cachedUserIndexer, cachedRepoIndexer, cachedIssueIndexer, cachedCommitIndexer),
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
            final CommitIndexer cacheOnlyCommitIndexer,
            final Exposer<CleanPullRequest> pullRequestExposer) {
        return new PullRequestExposerIndexer(
                new PullRequestIndexingService(postgresRawStorage, cacheOnlyUserIndexer, cacheOnlyRepoIndexer, cacheOnlyIssueIndexer, cacheOnlyCommitIndexer),
                pullRequestExposer
        );
    }

    @Bean
    public PullRequestIndexer livePullRequestIndexer(
            final RawStorageReader liveRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final RepoIndexer cachedRepoIndexer,
            final IssueIndexer cachedIssueIndexer,
            final CommitIndexer cachedCommitIndexer,
            final Exposer<CleanPullRequest> pullRequestExposer,
            final MeterRegistry registry) {
        return new PullRequestExposerIndexer(
                new MonitoredPullRequestIndexer(
                        new PullRequestIndexingService(liveRawStorageReader, cachedUserIndexer, cachedRepoIndexer, cachedIssueIndexer, cachedCommitIndexer),
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
    public JobManager cacheOnlyCommitRefreshJobManager(
            final PostgresCommitIndexingJobStorage commitIndexingJobStorage,
            final CommitIndexer cacheOnlyCommitIndexer,
            final UserFileExtensionStorage userFileExtensionStorage
    ) {
        return new CommitRefreshJobService(commitIndexingJobStorage, cacheOnlyCommitIndexer, userFileExtensionStorage);
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
    public UserPublicEventsIndexingJobManager userPublicEventsIndexingJobManager(
            final UserPublicEventsIndexingJobStorage userPublicEventsIndexingJobStorage,
            final UserPublicEventsIndexer cachedUserPublicEventsIndexer,
            final RawStorageReader cachedRawStorageReader
    ) {
        return new UserPublicEventsIndexingJobService(userPublicEventsIndexingJobStorage, cachedUserPublicEventsIndexer, cachedRawStorageReader);
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
    public Exposer<CleanCommit> commitExposer(final UserFileExtensionStorage userFileExtensionStorage,
                                              final CommitStorage commitStorage) {
        return new CommitExposer(userFileExtensionStorage, commitStorage);
    }

    @Bean
    public IndexingOutboxObserver indexingOutboxObserver(final OutboxPort outboxPort) {
        return new IndexingOutboxObserver(outboxPort);
    }

    @Bean
    public GithubOutboxObserver githubOutboxObserver(final OutboxPort outboxPort) {
        return new GithubOutboxObserver(outboxPort);
    }

    @Bean
    public JobManager commitIndexerJobManager(final CommitIndexer cachedCommitIndexer,
                                              final CommitIndexingJobStorage commitIndexingJobStorage,
                                              final RateLimitService rateLimitService
    ) {
        return new CommitIndexerJobService(cachedCommitIndexer, commitIndexingJobStorage, rateLimitService);
    }

    @Bean
    public CommitIndexer cachedCommitIndexer(final RawStorageReader cachedRawStorageReader,
                                             final UserIndexer cachedUserIndexer,
                                             final Exposer<CleanCommit> commitExposer) {
        return new CommitExposerIndexer(new CommitIndexingService(cachedRawStorageReader, cachedUserIndexer),
                commitExposer);
    }

    @Bean
    public CommitIndexer cacheOnlyCommitIndexer(final PostgresRawStorage postgresRawStorage,
                                                final UserIndexer cacheOnlyUserIndexer,
                                                final Exposer<CleanCommit> commitExposer) {
        return new CommitExposerIndexer(new CommitIndexingService(postgresRawStorage, cacheOnlyUserIndexer),
                commitExposer);
    }
}
