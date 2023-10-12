package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.models.UserIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.UserIndexingJobTriggerRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tags(@Tag(name = "Users"))
@AllArgsConstructor
public class IndexesRestApi implements IndexesApi {
    private final UserIndexingJobTriggerRepository userIndexingJobTriggerRepository;

    @Override
    public ResponseEntity<Void> addUserToIndex(Integer userId) {
        userIndexingJobTriggerRepository.add(new UserIndexingJobTrigger(userId.longValue()));
        return ResponseEntity.noContent().build();
    }
}
