package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoIndexingJobScheduler;
import com.onlydust.marketplace.indexer.rest.api.model.RepoLinkChangedEvent;
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
public class EventsRestApi implements EventsApi {
    private final RepoIndexingJobScheduler repoIndexingJobScheduler;

    @Override
    public ResponseEntity<Void> onRepoLinkChanged(RepoLinkChangedEvent event) {
        repoIndexingJobScheduler.addReposToRefresh(event.getLinkedRepoIds());
        repoIndexingJobScheduler.removeReposToRefresh(event.getUnlinkedRepoIds());
        return ResponseEntity.noContent().build();
    }
}
