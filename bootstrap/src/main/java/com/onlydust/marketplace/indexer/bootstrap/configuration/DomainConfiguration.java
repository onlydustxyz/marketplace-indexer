package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.ports.in.*;
import com.onlydust.marketplace.indexer.domain.ports.out.*;
import com.onlydust.marketplace.indexer.domain.services.*;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import com.onlydust.marketplace.indexer.github.adapters.GithubRawStorageReader;
import com.onlydust.marketplace.indexer.postgres.adapters.*;
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
                                                                   final PostgresGithubAccountRepository postgresGithubAccountRepository,
                                                                   final RepoIndexingJobRepository repoIndexingJobRepository) {
        return new InstallationEventProcessorService(postgresRawInstallationEventStorageRepository, cachedRawStorageReader, postgresGithubRepoRepository, postgresGithubAccountRepository, repoIndexingJobRepository);
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
    public IssueIndexer cachedIssueIndexer(final RawStorageReader cachedRawStorageReader, final UserIndexer cachedUserIndexer, final ContributionStorageRepository contributionStorageRepository) {
        return new IssueContributionExposer(
                new IssueIndexingService(cachedRawStorageReader, cachedUserIndexer),
                contributionStorageRepository
        );
    }

    @Bean(name = "issueIndexer")
    public IssueIndexer refreshingIssueIndexer(final RawStorageReader rawStorageReader, final UserIndexer userIndexer, final ContributionStorageRepository contributionStorageRepository) {
        return new IssueContributionExposer(
                new IssueIndexingService(rawStorageReader, userIndexer),
                contributionStorageRepository
        );
    }

    @Bean(name = "cachedPullRequestIndexer")
    public PullRequestIndexer cachedPullRequestIndexer(
            final RawStorageReader cachedRawStorageReader,
            final UserIndexer cachedUserIndexer,
            final IssueIndexer cachedIssueIndexer,
            final ContributionStorageRepository contributionStorageRepository) {
        return new PullRequestContributionExposer(
                new PullRequestIndexingService(cachedRawStorageReader, cachedUserIndexer, cachedIssueIndexer),
                contributionStorageRepository
        );
    }

    @Bean(name = "pullRequestIndexer")
    public PullRequestIndexer pullRequestIndexer(final RawStorageReader rawStorageReader,
                                                 final UserIndexer userIndexer,
                                                 final IssueIndexer issueIndexer,
                                                 final ContributionStorageRepository contributionStorageRepository) {
        return new PullRequestContributionExposer(
                new PullRequestIndexingService(rawStorageReader, userIndexer, issueIndexer),
                contributionStorageRepository
        );
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
    public RepoRefreshJobManager repoRefreshJobScheduler(
            final PostgresRepoIndexingJobRepository repoIndexingJobTriggerRepository,
            final RepoIndexer repoIndexer
    ) {
        return new RepoRefreshJobService(repoIndexingJobTriggerRepository, repoIndexer);
    }


    @Bean
    public UserRefreshJobManager userRefreshJobScheduler(
            final PostgresUserIndexingJobRepository userIndexingJobTriggerRepository,
            final UserIndexer userIndexer
    ) {
        return new UserRefreshJobService(userIndexingJobTriggerRepository, userIndexer);
    }
}
