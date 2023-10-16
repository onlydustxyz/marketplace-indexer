package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.models.UserIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobTriggerRepository;
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
    private final RepoIndexingJobTriggerRepository repoIndexingJobTriggerRepository;

    @Override
    public ResponseEntity<Void> addUserToIndex(Long userId) {
        userIndexingJobTriggerRepository.add(new UserIndexingJobTrigger(userId));
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> addRepoToIndex(Long repoId) {
        repoIndexingJobTriggerRepository.add(new RepoIndexingJobTrigger(0L, repoId));
        return ResponseEntity.noContent().build();
    }
}
