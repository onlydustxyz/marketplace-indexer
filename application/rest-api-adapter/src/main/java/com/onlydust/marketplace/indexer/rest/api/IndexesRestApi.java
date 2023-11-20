package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserRefreshJobManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.FutureTask;

@RestController
@Tags(@Tag(name = "Users"))
@AllArgsConstructor
@Slf4j
public class IndexesRestApi implements IndexesApi {
    private final TaskExecutor applicationTaskExecutor;
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

    @Override
    public ResponseEntity<Void> refreshExpositions() {
        final var jobs = cachedRepoRefreshJobManager.allJobs().stream().map(j -> new FutureTask<>(j::execute, null)).toList();
        jobs.forEach(applicationTaskExecutor::execute);
        jobs.forEach(j -> {
            try {
                j.get();
            } catch (Exception e) {
                throw OnlyDustException.internalServerError("Error refreshing expositions", e);
            }
        });
        return ResponseEntity.noContent().build();
    }
}
