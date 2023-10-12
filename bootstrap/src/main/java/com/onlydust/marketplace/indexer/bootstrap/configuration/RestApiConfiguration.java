package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.ports.out.UserIndexingJobTriggerRepository;
import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import com.onlydust.marketplace.indexer.rest.api.IndexesRestApi;
import com.onlydust.marketplace.indexer.rest.api.IssuesRestApi;
import com.onlydust.marketplace.indexer.rest.api.PullRequestsRestApi;
import com.onlydust.marketplace.indexer.rest.api.UsersRestApi;
import com.onlydust.marketplace.indexer.rest.api.exception.OnlyDustExceptionRestHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestApiConfiguration {
    @Bean
    public UsersRestApi usersRestApi(@Qualifier("onDemandIndexer") final IndexingService indexingService) {
        return new UsersRestApi(indexingService);
    }

    @Bean
    public IssuesRestApi issuesRestApi(@Qualifier("onDemandIndexer") final IndexingService indexingService) {
        return new IssuesRestApi(indexingService);
    }

    @Bean
    public PullRequestsRestApi pullRequestRestApi(@Qualifier("onDemandIndexer") final IndexingService indexingService) {
        return new PullRequestsRestApi(indexingService);
    }

    @Bean
    public IndexesRestApi indexesRestApi(final UserIndexingJobTriggerRepository userIndexingJobTriggerRepository) {
        return new IndexesRestApi(userIndexingJobTriggerRepository);
    }

    @Bean
    public OnlyDustExceptionRestHandler onlyDustExceptionRestHandler() {
        return new OnlyDustExceptionRestHandler();
    }
}
