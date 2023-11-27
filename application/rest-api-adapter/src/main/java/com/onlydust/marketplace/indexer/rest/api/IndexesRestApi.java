package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserIndexingJobScheduler;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tags(@Tag(name = "Users"))
@AllArgsConstructor
@Slf4j
public class IndexesRestApi implements IndexesApi {
    private final UserIndexingJobScheduler userIndexingJobScheduler;
    private final RepoIndexingJobScheduler repoIndexingJobScheduler;

    @Override
    public ResponseEntity<Void> addUserToIndex(Long userId) {
        userIndexingJobScheduler.addUserToRefresh(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> addRepoToIndex(Long repoId) {
        repoIndexingJobScheduler.addRepoToRefresh(repoId);
        return ResponseEntity.noContent().build();
    }
}
