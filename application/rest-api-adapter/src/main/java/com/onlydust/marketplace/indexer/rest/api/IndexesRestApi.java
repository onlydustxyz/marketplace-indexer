package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserRefreshJobManager;
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
    private final UserRefreshJobManager cachedUserRefreshJobManager;
    private final RepoRefreshJobManager cachedRepoRefreshJobManager;

    @Override
    public ResponseEntity<Void> addUserToIndex(Long userId) {
        cachedUserRefreshJobManager.addUserToRefresh(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> addRepoToIndex(Long repoId) {
        cachedRepoRefreshJobManager.addRepoToRefresh(repoId);
        return ResponseEntity.noContent().build();
    }
}
