package com.onlydust.marketplace.indexer.postgres;

import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.postgres.adapters.*;
import com.onlydust.marketplace.indexer.postgres.entities.ApiEventEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.ApiEventRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.UserIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.UserPublicEventsIndexingJobRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.*;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.*;
import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl;
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
}, repositoryBaseClass = BaseJpaRepositoryImpl.class)
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
                                                           final PullRequestClosingIssueRepository pullRequestClosingIssueRepository,
                                                           final PullRequestClosingIssueViewRepository pullRequestClosingIssueViewRepository,
                                                           final PullRequestReviewsRepository pullRequestReviewsRepository,
                                                           final PublicEventRepository publicEventRepository,
                                                           final CommitRepository commitRepository) {
        return new PostgresRawStorage(issueRepository,
                userRepository,
                repoRepository,
                pullRequestRepository,
                repoLanguagesRepository,
                userSocialAccountsRepository,
                pullRequestClosingIssueRepository,
                pullRequestClosingIssueViewRepository,
                pullRequestReviewsRepository,
                publicEventRepository,
                commitRepository);
    }

    @Bean
    public PostgresRepoIndexingJobStorage postgresRepoIndexingJobTriggerRepository(final RepoIndexingJobEntityRepository repoIndexingJobTriggerRepository) {
        return new PostgresRepoIndexingJobStorage(repoIndexingJobTriggerRepository);
    }

    @Bean
    public PostgresUserIndexingJobStorage userIndexingJobTriggerRepository(final UserIndexingJobEntityRepository userIndexingJobTriggerRepository) {
        return new PostgresUserIndexingJobStorage(userIndexingJobTriggerRepository);
    }

    @Bean
    public PostgresUserPublicEventsIndexingJobStorage userStatsIndexingJobStorage(final UserPublicEventsIndexingJobRepository userPublicEventsIndexingJobRepository) {
        return new PostgresUserPublicEventsIndexingJobStorage(userPublicEventsIndexingJobRepository);
    }

    @Bean
    public ContributionStorage contributionStorageRepository(final ContributionRepository contributionRepository,
                                                             final ContributionNotificationEntityRepository contributionNotificationEntityRepository) {
        return new PostgresContributionStorage(contributionRepository, contributionNotificationEntityRepository);
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
    public PostgresRepoStorage postgresRepoStorage(final GithubRepoRepository githubRepoRepository,
                                                   final GithubRepoStatsEntityRepository githubRepoStatsEntityRepository) {
        return new PostgresRepoStorage(githubRepoRepository, githubRepoStatsEntityRepository);
    }

    @Bean
    public PostgresAccountStorage accountStorage(final GithubAccountEntityRepository githubAccountEntityRepository) {
        return new PostgresAccountStorage(githubAccountEntityRepository);
    }

    @Bean
    public PostgresPullRequestStorage postgresPullRequestStorage(final GithubPullRequestRepository githubPullRequestRepository) {
        return new PostgresPullRequestStorage(githubPullRequestRepository);
    }

    @Bean
    public PostgresIssueStorage postgresIssueStorage(final GithubIssueRepository githubIssueRepository) {
        return new PostgresIssueStorage(githubIssueRepository);
    }

    @Bean
    public PostgresEventInboxStorage postgresEventInboxStorage(final EventsInboxEntityRepository eventsInboxEntityRepository) {
        return new PostgresEventInboxStorage(eventsInboxEntityRepository);
    }

    @Bean
    public PostgresOutboxAdapter<ApiEventEntity> apiEventEntityPostgresOutboxAdapter(final ApiEventRepository apiEventRepository) {
        return new PostgresOutboxAdapter<>(apiEventRepository);
    }

    @Bean
    public PostgresCommitIndexingJobStorage commitIndexingJobStorage(final CommitRepository commitRepository) {
        return new PostgresCommitIndexingJobStorage(commitRepository);
    }

    @Bean
    public PostgresUserFileExtensionsStorage postgresUserFileExtensionsStorage(final GithubUserFileExtensionsRepository githubUserFileExtensionsRepository) {
        return new PostgresUserFileExtensionsStorage(githubUserFileExtensionsRepository);
    }

    @Bean
    public PostgresCommitStorage postgresCommitStorage(final GithubCommitRepository githubCommitRepository) {
        return new PostgresCommitStorage(githubCommitRepository);
    }
}