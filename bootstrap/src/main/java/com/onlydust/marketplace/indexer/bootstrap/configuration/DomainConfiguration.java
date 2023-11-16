package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.events.InstallationEventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.*;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.*;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorageComposite;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.CacheReadRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.CacheWriteRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.DiffRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.services.events.InstallationEventProcessorService;
import com.onlydust.marketplace.indexer.domain.services.exposers.IssueContributionExposer;
import com.onlydust.marketplace.indexer.domain.services.exposers.PullRequestExposer;
import com.onlydust.marketplace.indexer.domain.services.exposers.RepoContributorsExposer;
import com.onlydust.marketplace.indexer.domain.services.exposers.RepoExposer;
import com.onlydust.marketplace.indexer.domain.services.indexers.*;
import com.onlydust.marketplace.indexer.domain.services.jobs.RepoRefreshJobService;
import com.onlydust.marketplace.indexer.domain.services.jobs.UserRefreshJobService;
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
    @ConfigurationProperties("application.repo-refresh-job")
    RepoRefreshJobService.Config repoRefreshJobConfig() {
        return new RepoRefreshJobService.Config();
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
    public InstallationEventHandler eventProcessorService(final PostgresRawInstallationEventStorageStorage postgresRawInstallationEventStorageRepository,
                                                          final PostgresRepoIndexingJobStorage repoIndexingJobRepository,
                                                          final PostgresOldRepoIndexingJobStorage oldRepoIndexesEntityRepository,
                                                          final UserIndexer cachedUserIndexer,
                                                          final RepoIndexer cachedRepoIndexer,
                                                          final GithubAppInstallationStorage githubAppInstallationStorage) {
        return new InstallationEventProcessorService(
                postgresRawInstallationEventStorageRepository,
                new RepoIndexingJobStorageComposite(repoIndexingJobRepository, oldRepoIndexesEntityRepository),
                cachedUserIndexer,
                cachedRepoIndexer,
                githubAppInstallationStorage);
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
    public UserIndexer diffUserIndexer(final RawStorageReader diffRawStorageReader, final MeterRegistry registry) {
        return new MonitoredUserIndexer(new UserIndexingService(diffRawStorageReader), registry);
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
    public IssueIndexer liveIssueIndexer(final RawStorageReader liveRawStorageReader,
                                         final UserIndexer cachedUserIndexer,
                                         final RepoIndexer cachedRepoIndexer,
                                         final ContributionStorage contributionStorage,
                                         final MeterRegistry registry) {
        return new IssueContributionExposer(
                new MonitoredIssueIndexer(
                        new IssueIndexingService(liveRawStorageReader, cachedUserIndexer, cachedRepoIndexer),
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
    public RepoIndexer liveRepoIndexer(
            final RawStorageReader liveRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final RepoStorage postgresRepoStorage) {
        return new RepoExposer(new RepoIndexingService(liveRawStorageReader, cachedUserIndexer), postgresRepoStorage);
    }


    @Bean
    public FullRepoIndexer cachedFullRepoIndexer(
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
    public FullRepoIndexer diffFullRepoIndexer(
            final RawStorageReader diffRawStorageReader,
            final IssueIndexer liveIssueIndexer,
            final PullRequestIndexer livePullRequestIndexer,
            final RepoIndexer liveRepoIndexer,
            final RepoContributorsStorage repoContributorsStorage) {
        return new RepoContributorsExposer(
                new FullRepoIndexingService(diffRawStorageReader, liveIssueIndexer, livePullRequestIndexer, liveRepoIndexer),
                repoContributorsStorage
        );
    }

    @Bean
    public RepoRefreshJobManager diffRepoRefreshJobManager(
            final PostgresRepoIndexingJobStorage repoIndexingJobTriggerRepository,
            final FullRepoIndexer diffFullRepoIndexer,
            final GithubAppContext githubAppContext,
            final RepoRefreshJobService.Config repoRefreshJobConfig
    ) {
        return new RepoRefreshJobService(repoIndexingJobTriggerRepository, diffFullRepoIndexer, githubAppContext, repoRefreshJobConfig);
    }

    @Bean
    public RepoRefreshJobManager cachedRepoRefreshJobManager(
            final PostgresRepoIndexingJobStorage repoIndexingJobTriggerRepository,
            final FullRepoIndexer cachedFullRepoIndexer,
            final GithubAppContext githubAppContext,
            final RepoRefreshJobService.Config repoRefreshJobConfig
    ) {
        return new RepoRefreshJobService(repoIndexingJobTriggerRepository, cachedFullRepoIndexer, githubAppContext, repoRefreshJobConfig);
    }

    @Bean
    public UserRefreshJobManager diffUserRefreshJobManager(
            final PostgresUserIndexingJobStorage userIndexingJobTriggerRepository,
            final UserIndexer diffUserIndexer
    ) {
        return new UserRefreshJobService(userIndexingJobTriggerRepository, diffUserIndexer);
    }

    @Bean
    public UserRefreshJobManager cachedUserRefreshJobManager(
            final PostgresUserIndexingJobStorage userIndexingJobTriggerRepository,
            final UserIndexer cachedUserIndexer
    ) {
        return new UserRefreshJobService(userIndexingJobTriggerRepository, cachedUserIndexer);
    }
}
