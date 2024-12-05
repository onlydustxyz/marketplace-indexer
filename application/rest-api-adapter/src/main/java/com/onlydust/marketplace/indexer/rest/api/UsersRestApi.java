package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.AuthorizationContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobExecutor;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserPublicEventsIndexingJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.noContent;

@RestController
@AllArgsConstructor
@Profile("api")
public class UsersRestApi implements UsersApi {
    private final UserIndexer cachedUserIndexer;
    private final AuthorizationContext authorizationContext;
    private final UserIndexingJobStorage userIndexingJobStorage;
    private final UserPublicEventsIndexingJobManager userPublicEventsIndexingJobManager;
    private final JobExecutor jobExecutor;

    @Override
    public ResponseEntity<Void> indexUser(Long userId, String authorization) {
        userIndexingJobStorage.add(userId);

        authorizationContext.withAuthorization(authorization,
                () -> {
                    cachedUserIndexer.indexUser(userId);
                    jobExecutor.execute(userPublicEventsIndexingJobManager.name(userId), "user_public_event_indexer", userId.toString());
                });
        return noContent().build();
    }
}
