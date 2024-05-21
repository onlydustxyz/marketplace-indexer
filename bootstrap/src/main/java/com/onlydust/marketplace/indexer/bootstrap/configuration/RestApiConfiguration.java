package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.AuthorizationContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.services.indexers.UserExposerIndexer;
import com.onlydust.marketplace.indexer.github.adapters.GithubAppContextAdapter;
import com.onlydust.marketplace.indexer.rest.api.*;
import com.onlydust.marketplace.indexer.rest.api.exception.OnlyDustExceptionRestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("api")
public class RestApiConfiguration {
    @Bean
    public UsersRestApi usersRestApi(final UserIndexer cachedUserIndexer, final AuthorizationContext authorizationContext,
                                     final Exposer<CleanAccount> userExposer, final UserIndexingJobStorage userIndexingJobStorage) {
        return new UsersRestApi(new UserExposerIndexer(cachedUserIndexer, userExposer), authorizationContext, userIndexingJobStorage);
    }

    @Bean
    public IssuesRestApi issuesRestApi(final IssueIndexer cachedIssueIndexer, final AuthorizationContext authorizationContext) {
        return new IssuesRestApi(cachedIssueIndexer, authorizationContext);
    }

    @Bean
    public PullRequestsRestApi pullRequestRestApi(final PullRequestIndexer cachedPullRequestIndexer, final AuthorizationContext authorizationContext) {
        return new PullRequestsRestApi(cachedPullRequestIndexer, authorizationContext);
    }

    @Bean
    public IndexesRestApi indexesRestApi(final UserIndexingJobScheduler userIndexingJobScheduler) {
        return new IndexesRestApi(userIndexingJobScheduler);
    }

    @Bean
    public EventsRestApi eventsRestApi(final RepoIndexingJobScheduler repoIndexingJobScheduler, final UserIndexer diffUserIndexer) {
        return new EventsRestApi(repoIndexingJobScheduler, diffUserIndexer);
    }

    @Bean
    public DebugRestApi debugRestApi(final GithubAppContextAdapter githubAppContextAdapter) {
        return new DebugRestApi(githubAppContextAdapter);
    }

    @Bean
    public OnlyDustExceptionRestHandler onlyDustExceptionRestHandler() {
        return new OnlyDustExceptionRestHandler();
    }
}
