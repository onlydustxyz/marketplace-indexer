package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.AuthorizationContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.AccountStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.services.exposers.UserExposer;
import com.onlydust.marketplace.indexer.rest.api.IndexesRestApi;
import com.onlydust.marketplace.indexer.rest.api.IssuesRestApi;
import com.onlydust.marketplace.indexer.rest.api.PullRequestsRestApi;
import com.onlydust.marketplace.indexer.rest.api.UsersRestApi;
import com.onlydust.marketplace.indexer.rest.api.exception.OnlyDustExceptionRestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("api")
public class RestApiConfiguration {
    @Bean
    public UsersRestApi usersRestApi(final UserIndexer cachedUserIndexer,
                                     final AuthorizationContext authorizationContext,
                                     final AccountStorage accountStorage,
                                     final UserIndexingJobStorage userIndexingJobStorage) {
        return new UsersRestApi(
                new UserExposer(cachedUserIndexer, accountStorage),
                authorizationContext,
                userIndexingJobStorage);
    }

    @Bean
    public IssuesRestApi issuesRestApi(final IssueIndexer cachedIssueIndexer,
                                       final AuthorizationContext authorizationContext) {
        return new IssuesRestApi(cachedIssueIndexer, authorizationContext);
    }

    @Bean
    public PullRequestsRestApi pullRequestRestApi(final PullRequestIndexer cachedPullRequestIndexer,
                                                  final AuthorizationContext authorizationContext) {
        return new PullRequestsRestApi(cachedPullRequestIndexer, authorizationContext);
    }

    @Bean
    public IndexesRestApi indexesRestApi(final UserIndexingJobScheduler userIndexingJobScheduler,
                                         final RepoIndexingJobScheduler repoIndexingJobScheduler) {
        return new IndexesRestApi(userIndexingJobScheduler, repoIndexingJobScheduler);
    }

    @Bean
    public OnlyDustExceptionRestHandler onlyDustExceptionRestHandler() {
        return new OnlyDustExceptionRestHandler();
    }
}
