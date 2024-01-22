package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoIndexingJobScheduler;
import com.onlydust.marketplace.indexer.rest.api.model.RepoLinkChangedEvent;
import com.onlydust.marketplace.indexer.rest.api.model.UserChangedEvent;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Tags(@Tag(name = "Users"))
@AllArgsConstructor
@Slf4j
public class EventsRestApi implements EventsApi {
    private final RepoIndexingJobScheduler repoIndexingJobScheduler;
    private final UserIndexer diffUserIndexer;

    @Override
    public ResponseEntity<Void> onRepoLinkChanged(RepoLinkChangedEvent event) {
        repoIndexingJobScheduler.addReposToRefresh(Optional.ofNullable(event.getLinkedRepoIds()).orElse(List.of()));
        repoIndexingJobScheduler.removeReposToRefresh(Optional.ofNullable(event.getUnlinkedRepoIds()).orElse(List.of()));
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> onUserChanged(UserChangedEvent event) {
        event.getUserIds().forEach(diffUserIndexer::indexUser);
        return ResponseEntity.noContent().build();
    }
}
