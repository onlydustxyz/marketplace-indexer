package com.onlydust.marketplace.indexer.postgres;

import com.onlydust.marketplace.indexer.domain.ports.out.ContributionStorageRepository;
import com.onlydust.marketplace.indexer.postgres.adapters.*;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobTriggerEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.UserIndexingJobTriggerEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = {
        "com.onlydust.marketplace.indexer.postgres.entities"
})
@EnableJpaRepositories(basePackages = {
        "com.onlydust.marketplace.indexer.postgres.repositories"
})
@EnableTransactionManagement
@EnableJpaAuditing
public class PostgresConfiguration {
    @Bean
    public PostgresRawStorageRepository postgresRawStorageRepository(final IssueRepository issueRepository,
                                                                     final UserRepository userRepository,
                                                                     final RepoRepository repoRepository,
                                                                     final PullRequestRepository pullRequestRepository,
                                                                     final RepoLanguagesRepository repoLanguagesRepository,
                                                                     final UserSocialAccountsRepository userSocialAccountsRepository,
                                                                     final PullRequestCommitsRepository pullRequestCommitsRepository,
                                                                     final PullRequestClosingIssueRepository pullRequestClosingIssueRepository,
                                                                     final PullRequestClosingIssueViewRepository pullRequestClosingIssueViewRepository,
                                                                     final PullRequestReviewsRepository pullRequestReviewsRepository,
                                                                     final RepoCheckRunsRepository repoCheckRunsRepository) {
        return new PostgresRawStorageRepository(issueRepository,
                userRepository,
                repoRepository,
                pullRequestRepository,
                repoLanguagesRepository,
                userSocialAccountsRepository,
                pullRequestCommitsRepository,
                pullRequestClosingIssueRepository,
                pullRequestClosingIssueViewRepository,
                pullRequestReviewsRepository,
                repoCheckRunsRepository
        );
    }

    @Bean
    public PostgresRawInstallationEventStorageRepository postgresRawInstallationEventStorageRepository(final InstallationEventRepository installationEventRepository) {
        return new PostgresRawInstallationEventStorageRepository(installationEventRepository);
    }

    @Bean
    public PostgresRepoIndexingJobRepository postgresRepoIndexingJobTriggerRepository(final RepoIndexingJobTriggerEntityRepository repoIndexingJobTriggerRepository) {
        return new PostgresRepoIndexingJobRepository(repoIndexingJobTriggerRepository);
    }

    @Bean
    public PostgresUserIndexingJobRepository userIndexingJobTriggerRepository(final UserIndexingJobTriggerEntityRepository userIndexingJobTriggerRepository) {
        return new PostgresUserIndexingJobRepository(userIndexingJobTriggerRepository);
    }

    @Bean
    public ContributionStorageRepository contributionStorageRepository(final ContributionRepository contributionRepository) {
        return new PostgresContributionStorageRepository(contributionRepository);
    }

    @Bean
    public PostgresGithubRepoRepository postgresGithubRepoRepository(final GithubRepoEntityRepository githubRepoRepository) {
        return new PostgresGithubRepoRepository(githubRepoRepository);
    }
}