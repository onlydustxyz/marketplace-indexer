package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.raw.RawRepositoryEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class RepositoryEventProcessorServiceTest {
    final RepoIndexingJobStorage repoIndexingJobStorage = mock(RepoIndexingJobStorage.class);
    final RepoStorage githubRepoStorage = mock(RepoStorage.class);
    final RawStorageWriter rawStorageWriter = mock(RawStorageWriter.class);
    final RepoIndexer repoIndexer = mock(RepoIndexer.class);
    final RepositoryEventProcessorService repositoryEventProcessorService = new RepositoryEventProcessorService(repoIndexingJobStorage, githubRepoStorage, rawStorageWriter, repoIndexer);

    @Test
    void should_handle_repo_edited_event() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/repository/cairo-streams-edited.json", RawRepositoryEvent.class);

        // When
        repositoryEventProcessorService.process(event);

        // Then
        verify(githubRepoStorage, never()).save(any());
        verify(repoIndexer).indexRepo(493795808L);
    }

    @Test
    void should_handle_repo_becoming_private() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/repository/cairo-streams-privatized.json", RawRepositoryEvent.class);

        // When
        repositoryEventProcessorService.process(event);

        // Then
        verify(githubRepoStorage, never()).save(any());
        verify(repoIndexer).indexRepo(493795808L);
        verify(repoIndexingJobStorage).setPrivate(493795808L);
    }

    @Test
    void should_handle_repo_becoming_public() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/repository/cairo-streams-publicized.json", RawRepositoryEvent.class);

        // When
        repositoryEventProcessorService.process(event);

        // Then
        verify(githubRepoStorage, never()).save(any());
        verify(repoIndexer).indexRepo(493795808L);
        verify(repoIndexingJobStorage).setPublic(493795808L);
    }

    @Test
    void should_handle_repo_being_deleted() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/repository/cairo-streams-deleted.json", RawRepositoryEvent.class);

        // When
        repositoryEventProcessorService.process(event);

        // Then
        verify(githubRepoStorage).save(argThat(repo -> repo.getId().equals(493795808L) && repo.getDeletedAt().equals(event.getRepository().getUpdatedAt())));
        verify(repoIndexer, never()).indexRepo(any());
        verify(repoIndexingJobStorage).delete(493795808L);
        verify(rawStorageWriter).deleteRepo(493795808L);
    }
}