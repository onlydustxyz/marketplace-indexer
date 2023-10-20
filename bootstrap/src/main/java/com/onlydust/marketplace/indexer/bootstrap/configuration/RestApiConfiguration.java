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
    public UsersRestApi usersRestApi(final UserIndexer cachedUserIndexer) {
        return new UsersRestApi(cachedUserIndexer);
    }

    @Bean
    public IssuesRestApi issuesRestApi(final IssueIndexer cachedIssueIndexer) {
        return new IssuesRestApi(cachedIssueIndexer);
    }

    @Bean
    public PullRequestsRestApi pullRequestRestApi(final PullRequestIndexer cachedPullRequestIndexer) {
        return new PullRequestsRestApi(cachedPullRequestIndexer);
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
