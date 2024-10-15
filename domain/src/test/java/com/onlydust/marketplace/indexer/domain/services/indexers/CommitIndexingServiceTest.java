package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.github.javafaker.Faker;
import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

class CommitIndexingServiceTest {
    private final static Faker faker = new Faker();
    private final RawStorageReader rawStorageReader = mock(RawStorageReader.class);
    private final UserIndexer userIndexer = mock(UserIndexer.class);
    private final CommitIndexingService commitIndexingService = new CommitIndexingService(rawStorageReader, userIndexer);
    private final Long repoId = faker.random().nextLong();
    private final String sha = faker.random().hex();

    @BeforeEach
    void setUp() {
        reset(rawStorageReader, userIndexer);
    }

    @Test
    void should_index_commit_with_author() {
        // Given
        final var commit = RawStorageWriterStub.load("/github/commits/with_author.json", RawCommit.class);

        when(rawStorageReader.commit(repoId, sha)).thenReturn(Optional.of(commit));

        // When
        commitIndexingService.indexCommit(repoId, sha);

        // Then
        verify(userIndexer).indexUser(commit.getAuthor().getId());
    }


    @Test
    void should_index_commit_without_author() {
        // Given
        final var commit = RawStorageWriterStub.load("/github/commits/without_author.json", RawCommit.class);

        // When
        commitIndexingService.indexCommit(repoId, sha);

        // Then
        verifyNoInteractions(userIndexer);
    }
}