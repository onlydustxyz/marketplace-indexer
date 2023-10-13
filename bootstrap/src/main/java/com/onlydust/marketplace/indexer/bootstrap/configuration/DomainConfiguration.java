package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJob;
import com.onlydust.marketplace.indexer.domain.models.UserIndexingJob;
import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.*;
import com.onlydust.marketplace.indexer.domain.ports.out.*;
import com.onlydust.marketplace.indexer.domain.services.*;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import com.onlydust.marketplace.indexer.github.adapters.GithubRawStorageReader;
import com.onlydust.marketplace.indexer.postgres.adapters.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.util.List;

@Configuration
public class DomainConfiguration {
    @Bean
    @ConfigurationProperties("infrastructure.github")
    GithubHttpClient.Config githubConfig() {
        return new GithubHttpClient.Config();
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

    @Bean(name = "installationEventEventListener")
    public EventListener<InstallationEvent> installationEventEventListener(
            final PostgresInstallationEventListener postgresInstallationEventListener,
            final JobTriggerEventListener jobTriggerEventListener) {
        return new EventListenerComposite<>(List.of(postgresInstallationEventListener, jobTriggerEventListener));
    }

    @Bean
    public EventProcessorService eventProcessorService(final PostgresRawInstallationEventStorageRepository postgresRawInstallationEventStorageRepository,
                                                       @Qualifier("installationEventEventListener") final EventListener<InstallationEvent> eventListener,
                                                       final RawStorageReader cachedRawStorageReader) {
        return new EventProcessorService(postgresRawInstallationEventStorageRepository, eventListener, cachedRawStorageReader);
    }

    @Bean
    public GithubHttpClient githubHttpClient(final ObjectMapper objectMapper, final HttpClient httpClient, final GithubHttpClient.Config config) {
        return new GithubHttpClient(objectMapper, httpClient, config);
    }

    @Bean
    GithubRawStorageReader githubRawStorageReader(final GithubHttpClient githubHttpClient) {
        return new GithubRawStorageReader(githubHttpClient);
    }

    @Bean(name = "cachedUserIndexer")
    public UserIndexer cachedUserIndexer(final RawStorageReader cachedRawStorageReader) {
        return new UserIndexingService(cachedRawStorageReader);
    }

    @Bean(name = "userIndexer")
    public UserIndexer refreshingUserIndexer(final RawStorageReader rawStorageReader) {
        return new UserIndexingService(rawStorageReader);
    }

    @Bean(name = "cachedIssueIndexer")
    public IssueIndexer cachedIssueIndexer(final RawStorageReader cachedRawStorageReader, final UserIndexer cachedUserIndexer) {
        return new IssueIndexingService(cachedRawStorageReader, cachedUserIndexer);
    }

    @Bean(name = "issueIndexer")
    public IssueIndexer refreshingIssueIndexer(final RawStorageReader rawStorageReader, final UserIndexer userIndexer) {
        return new IssueIndexingService(rawStorageReader, userIndexer);
    }

    @Bean(name = "cachedPullRequestIndexer")
    public PullRequestIndexer cachedPullRequestIndexer(
            final RawStorageReader cachedRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final IssueIndexer cachedIssueIndexer) {
        return new PullRequestIndexingService(cachedRawStorageReader, cachedUserIndexer, cachedIssueIndexer);
    }

    @Bean(name = "pullRequestIndexer")
    public PullRequestIndexingService pullRequestIndexer(final RawStorageReader rawStorageReader,
                                                         final UserIndexer userIndexer,
                                                         final IssueIndexer issueIndexer) {
        return new PullRequestIndexingService(rawStorageReader, userIndexer, issueIndexer);
    }

    @Bean(name = "cachedRepoIndexer")
    public RepoIndexer cachedRepoIndexer(
            final RawStorageReader cachedRawStorageReader,
            final IssueIndexer cachedIssueIndexer,
            final PullRequestIndexer cachedPullRequestIndexer) {
        return new RepoIndexingService(cachedRawStorageReader, cachedIssueIndexer, cachedPullRequestIndexer);
    }

    @Bean(name = "repoIndexer")
    public RepoIndexer repoIndexer(
            final RawStorageReader rawStorageReader,
            final IssueIndexer issueIndexer,
            final PullRequestIndexer pullRequestIndexer) {
        return new RepoIndexingService(rawStorageReader, issueIndexer, pullRequestIndexer);
    }

    @Bean
    public RefreshJobScheduler repoRefreshJobScheduler(
            final PostgresRepoIndexingJobTriggerRepository repoIndexingJobTriggerRepository,
            final JobScheduler<RepoIndexingJob> scheduler
    ) {
        return new RepoRefreshJobService(repoIndexingJobTriggerRepository, scheduler);
    }


    @Bean
    public RefreshJobScheduler userRefreshJobScheduler(
            final PostgresUserIndexingJobTriggerRepository userIndexingJobTriggerRepository,
            final JobScheduler<UserIndexingJob> scheduler
    ) {
        return new UserRefreshJobService(userIndexingJobTriggerRepository, scheduler);
    }
}
