package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import com.onlydust.marketplace.indexer.rest.api.IssuesRestApi;
import com.onlydust.marketplace.indexer.rest.api.PullRequestsRestApi;
import com.onlydust.marketplace.indexer.rest.api.UsersRestApi;
import com.onlydust.marketplace.indexer.rest.api.exception.OnlyDustExceptionRestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestApiConfiguration {
    @Bean
    public UsersRestApi usersRestApi(final IndexingService indexingService) {
        return new UsersRestApi(indexingService);
    }

    @Bean
    public IssuesRestApi issuesRestApi(final IndexingService indexingService) {
        return new IssuesRestApi(indexingService);
    }

    @Bean
    public PullRequestsRestApi pullRequestRestApi(final IndexingService indexingService) {
        return new PullRequestsRestApi(indexingService);
    }

    @Bean
    public OnlyDustExceptionRestHandler onlyDustExceptionRestHandler() {
        return new OnlyDustExceptionRestHandler();
    }
}
