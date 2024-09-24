package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.events.RepositoryEvent;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepositoryEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Transactional
public class RepositoryEventProcessorService implements EventHandler<RawRepositoryEvent> {
    private final RepoIndexingJobStorage repoIndexingJobStorage;
    private final RepoStorage githubRepoStorage;
    private final RawStorageWriter rawStorageWriter;
    private final RepoIndexer repoIndexer;

    @Override
    public void process(RawRepositoryEvent rawEvent) {
        final var event = RepositoryEvent.of(rawEvent);

        if (RepositoryEvent.Action.DELETED.equals(event.getAction())) {
            githubRepoStorage.save(GithubRepo.of(event.getRepository()).deleted());
        } else if (!RepositoryEvent.Action.CREATED.equals(event.getAction())) {
            repoIndexer.indexRepo(event.getRepository().getId());
        }

        if (event.getAction() == null)
            return;

        switch (event.getAction()) {
            case PRIVATIZED -> repoIndexingJobStorage.setPrivate(event.getRepository().getId());
            case PUBLICIZED -> repoIndexingJobStorage.setPublic(event.getRepository().getId());
            case DELETED -> {
                repoIndexingJobStorage.delete(event.getRepository().getId());
                rawStorageWriter.deleteRepo(event.getRepository().getId());
            }
        }
    }
}
