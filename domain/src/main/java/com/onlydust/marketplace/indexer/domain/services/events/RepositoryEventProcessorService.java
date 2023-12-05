package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.RepositoryEvent;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepositoryEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

@AllArgsConstructor
@Slf4j
@Transactional
public class RepositoryEventProcessorService implements EventHandler<RawRepositoryEvent> {
    private final RepoIndexingJobStorage repoIndexingJobStorage;
    private final RepoStorage githubRepoStorage;


    @Override
    public void process(RawRepositoryEvent rawEvent) {
        final var event = RepositoryEvent.of(rawEvent);
        githubRepoStorage.update(GithubRepo.of(event.getRepository()));

        if (event.getAction() == null) return;
        switch (event.getAction()) {
            case PRIVATIZED -> repoIndexingJobStorage.setPrivate(event.getRepository().getId());
            case PUBLICIZED -> repoIndexingJobStorage.setPublic(event.getRepository().getId());
        }
    }
}
