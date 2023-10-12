package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tags(@Tag(name = "Users"))
@AllArgsConstructor
public class UsersRestApi implements UsersApi {
    private final IndexingService onDemandIndexer;

    @Override
    public ResponseEntity<Void> indexUser(Long userId) {
        onDemandIndexer.indexUser(userId);
        return ResponseEntity.noContent().build();
    }
}
