package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.AuthorizationContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserPublicEventsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.noContent;

@RestController
@Tags(@Tag(name = "Users"))
@AllArgsConstructor
public class UsersRestApi implements UsersApi {
    private final UserIndexer cachedUserIndexer;
    private final UserPublicEventsIndexer cachedUserPublicEventsIndexer;
    private final AuthorizationContext authorizationContext;
    private final UserIndexingJobStorage userIndexingJobStorage;

    @Override
    public ResponseEntity<Void> indexUser(Long userId, String authorization) {
        userIndexingJobStorage.add(userId);
        authorizationContext.withAuthorization(authorization,
                () -> cachedUserIndexer.indexUser(userId)
                        .ifPresent(user -> cachedUserPublicEventsIndexer.indexUser(user.getId(), user.getCreatedAt())));
        return noContent().build();
    }
}
