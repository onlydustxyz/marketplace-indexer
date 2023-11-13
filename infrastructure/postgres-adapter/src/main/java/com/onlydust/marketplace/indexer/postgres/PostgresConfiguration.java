package com.onlydust.marketplace.indexer.postgres;

import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.postgres.adapters.*;
import com.onlydust.marketplace.indexer.postgres.repositories.OldRepoIndexesEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.UserIndexingJobTriggerEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAppInstallationEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.RepoContributorRepository;
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
    public PostgresRawStorage postgresRawStorageRepository(final IssueRepository issueRepository,
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
        return new PostgresRawStorage(issueRepository,
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
    public PostgresRawInstallationEventStorageStorage postgresRawInstallationEventStorageRepository(final InstallationEventRepository installationEventRepository) {
        return new PostgresRawInstallationEventStorageStorage(installationEventRepository);
    }

    @Bean
    public PostgresRepoIndexingJobStorage postgresRepoIndexingJobTriggerRepository(final RepoIndexingJobEntityRepository repoIndexingJobTriggerRepository) {
        return new PostgresRepoIndexingJobStorage(repoIndexingJobTriggerRepository);
    }

    @Bean
    public PostgresOldRepoIndexingJobStorage postgresOldRepoIndexingJobRepository(final OldRepoIndexesEntityRepository oldRepoIndexesEntityRepository) {
        return new PostgresOldRepoIndexingJobStorage(oldRepoIndexesEntityRepository);
    }


    @Bean
    public PostgresUserIndexingJobStorage userIndexingJobTriggerRepository(final UserIndexingJobTriggerEntityRepository userIndexingJobTriggerRepository) {
        return new PostgresUserIndexingJobStorage(userIndexingJobTriggerRepository);
    }

    @Bean
    public ContributionStorage contributionStorageRepository(final ContributionRepository contributionRepository) {
        return new PostgresContributionStorage(contributionRepository);
    }

    @Bean
    public PostgresGithubAppInstallationStorage postgresGithubAppInstallationRepository(final GithubAppInstallationEntityRepository githubAppInstallationEntityRepository) {
        return new PostgresGithubAppInstallationStorage(githubAppInstallationEntityRepository);
    }

    @Bean
    public PostgresRepoContributorsStorage postgresRepoContributorsStorage(final RepoContributorRepository repoContributorRepository) {
        return new PostgresRepoContributorsStorage(repoContributorRepository);
    }

    @Bean
    public PostgresRepoStorage postgresRepoStorage(final GithubRepoEntityRepository githubRepoEntityRepository) {
        return new PostgresRepoStorage(githubRepoEntityRepository);
    }
}