package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.ports.in.RepoRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.UserRefreshJobManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tags(@Tag(name = "Users"))
@AllArgsConstructor
public class IndexesRestApi implements IndexesApi {
    private final UserRefreshJobManager userRefresher;
    private final RepoRefreshJobManager repoRefreshJobManager;

    @Override
    public ResponseEntity<Void> addUserToIndex(Long userId) {
        userRefresher.addUserToRefresh(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> addRepoToIndex(Long repoId) {
        repoRefreshJobManager.addRepoToRefresh(repoId);
        return ResponseEntity.noContent().build();
    }
}
