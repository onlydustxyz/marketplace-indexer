package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.ports.in.*;
import com.onlydust.marketplace.indexer.rest.api.IndexesRestApi;
import com.onlydust.marketplace.indexer.rest.api.IssuesRestApi;
import com.onlydust.marketplace.indexer.rest.api.PullRequestsRestApi;
import com.onlydust.marketplace.indexer.rest.api.UsersRestApi;
import com.onlydust.marketplace.indexer.rest.api.exception.OnlyDustExceptionRestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;

@Configuration
public class RestApiConfiguration {
    @Bean
    public UsersRestApi usersRestApi(final UserIndexer cachedUserIndexer,
                                     final AuthorizationContext authorizationContext) {
        return new UsersRestApi(cachedUserIndexer, authorizationContext);
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
    public IndexesRestApi indexesRestApi(final UserRefreshJobManager userCacheRefreshJobScheduler,
                                         final RepoRefreshJobManager repoCacheRefreshJobScheduler,
                                         final Executor applicationTaskExecutor) {
        return new IndexesRestApi(applicationTaskExecutor, userCacheRefreshJobScheduler, repoCacheRefreshJobScheduler);
    }

    @Bean
    public OnlyDustExceptionRestHandler onlyDustExceptionRestHandler() {
        return new OnlyDustExceptionRestHandler();
    }
}
