package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.ports.in.*;
import com.onlydust.marketplace.indexer.domain.ports.out.*;
import com.onlydust.marketplace.indexer.domain.services.*;
import com.onlydust.marketplace.indexer.domain.services.exposers.IssueContributionExposer;
import com.onlydust.marketplace.indexer.domain.services.exposers.PullRequestContributionExposer;
import com.onlydust.marketplace.indexer.domain.services.monitoring.MonitoredFullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.services.monitoring.MonitoredIssueIndexer;
import com.onlydust.marketplace.indexer.domain.services.monitoring.MonitoredPullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.services.monitoring.MonitoredUserIndexer;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import com.onlydust.marketplace.indexer.github.adapters.GithubRateLimitServiceAdapter;
import com.onlydust.marketplace.indexer.github.adapters.GithubRawStorageReader;
import com.onlydust.marketplace.indexer.postgres.adapters.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class DomainConfiguration {
    @Bean
    @ConfigurationProperties("infrastructure.github")
    GithubHttpClient.Config githubConfig() {
        return new GithubHttpClient.Config();
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
            final PostgresRawStorageRepository postgresRawStorageRepository
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
            final PostgresRawStorageRepository postgresRawStorageRepository
    ) {
        return CacheWriteRawStorageReaderDecorator.builder()
                .fetcher(githubRawStorageReader)
                .cache(postgresRawStorageRepository)
                .build();
    }

    @Bean
    public InstallationEventProcessorService eventProcessorService(final PostgresRawInstallationEventStorageRepository postgresRawInstallationEventStorageRepository,
                                                                   final RawStorageReader cachedRawStorageReader,
                                                                   final PostgresGithubRepoRepository postgresGithubRepoRepository,
                                                                   final PostgresRepoIndexingJobRepository repoIndexingJobRepository,
                                                                   final PostgresOldRepoIndexingJobRepository oldRepoIndexesEntityRepository,
                                                                   final UserIndexer cachedUserIndexer,
                                                                   final RepoIndexer cachedRepoIndexer,
                                                                   final GithubAppInstallationRepository githubAppInstallationRepository) {
        return new InstallationEventProcessorService(postgresRawInstallationEventStorageRepository,
                cachedRawStorageReader,
                postgresGithubRepoRepository,
                new RepoIndexingJobRepositoryComposite(repoIndexingJobRepository, oldRepoIndexesEntityRepository),
                cachedUserIndexer,
                cachedRepoIndexer,
                githubAppInstallationRepository);
    }

    @Bean
    public GithubHttpClient githubHttpClient(final ObjectMapper objectMapper, final HttpClient httpClient, final GithubHttpClient.Config config) {
        return new GithubHttpClient(objectMapper, httpClient, config);
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
                                           final ContributionStorageRepository contributionStorageRepository,
                                           final MeterRegistry registry) {
        return new IssueContributionExposer(
                new MonitoredIssueIndexer(
                        new IssueIndexingService(cachedRawStorageReader, cachedUserIndexer, cachedRepoIndexer),
                        registry
                ),
                contributionStorageRepository
        );
    }


    @Bean
    public PullRequestIndexer cachedPullRequestIndexer(
            final RawStorageReader cachedRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final RepoIndexer cachedRepoIndexer,
            final IssueIndexer cachedIssueIndexer,
            final ContributionStorageRepository contributionStorageRepository,
            final MeterRegistry registry) {
        return new PullRequestContributionExposer(
                new MonitoredPullRequestIndexer(
                        new PullRequestIndexingService(cachedRawStorageReader, cachedUserIndexer, cachedRepoIndexer, cachedIssueIndexer),
                        registry),
                contributionStorageRepository
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
            final PostgresRepoIndexingJobRepository repoIndexingJobTriggerRepository,
            final FullRepoIndexer refreshingFullRepoIndexer
    ) {
        return new RepoRefreshJobService(repoIndexingJobTriggerRepository, refreshingFullRepoIndexer);
    }

    @Bean
    public RepoRefreshJobManager repoCacheRefreshJobScheduler(
            final PostgresRepoIndexingJobRepository repoIndexingJobTriggerRepository,
            final FullRepoIndexer cachedFullRepoIndexer
    ) {
        return new RepoRefreshJobService(repoIndexingJobTriggerRepository, cachedFullRepoIndexer);
    }

    @Bean
    public UserRefreshJobManager userRefreshJobScheduler(
            final PostgresUserIndexingJobRepository userIndexingJobTriggerRepository,
            final UserIndexer refreshingUserIndexer
    ) {
        return new UserRefreshJobService(userIndexingJobTriggerRepository, refreshingUserIndexer);
    }

    @Bean
    public UserRefreshJobManager userCacheRefreshJobScheduler(
            final PostgresUserIndexingJobRepository userIndexingJobTriggerRepository,
            final UserIndexer cachedUserIndexer
    ) {
        return new UserRefreshJobService(userIndexingJobTriggerRepository, cachedUserIndexer);
    }
}
