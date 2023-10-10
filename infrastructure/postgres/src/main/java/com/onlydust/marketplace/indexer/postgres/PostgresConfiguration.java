package com.onlydust.marketplace.indexer.postgres;

import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRawInstallationEventStorageRepository;
import com.onlydust.marketplace.indexer.postgres.adapters.PostgresRawStorageRepository;
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
                pullRequestReviewsRepository,
                repoCheckRunsRepository
        );
    }

    @Bean
    public PostgresRawInstallationEventStorageRepository postgresRawInstallationEventStorageRepository(final InstallationEventRepository installationEventRepository) {
        return new PostgresRawInstallationEventStorageRepository(installationEventRepository);
    }
}