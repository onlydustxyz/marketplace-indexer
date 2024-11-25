package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.github_app_events.RepositoryEvent;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawRepositoryEvent;
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
        rawStorageWriter.saveRepo(rawEvent.getRepository());
        
        final var event = RepositoryEvent.of(rawEvent);

        switch (event.getAction()) {
            case "created" -> {
            }

            case "deleted" -> {
                githubRepoStorage.save(GithubRepo.of(event.getRepository()).deleted());
                repoIndexingJobStorage.delete(event.getRepository().getId());
                rawStorageWriter.deleteRepo(event.getRepository().getId());
            }

            case "privatized" -> {
                repoIndexer.indexRepo(event.getRepository().getId());
                repoIndexingJobStorage.setPrivate(event.getRepository().getId());
            }

            case "publicized" -> {
                repoIndexer.indexRepo(event.getRepository().getId());
                repoIndexingJobStorage.setPublic(event.getRepository().getId());
            }

            default -> repoIndexer.indexRepo(event.getRepository().getId());
        }
    }
}
