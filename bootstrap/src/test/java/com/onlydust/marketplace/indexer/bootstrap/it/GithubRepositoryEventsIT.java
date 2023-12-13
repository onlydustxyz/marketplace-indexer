package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.postgres.repositories.OldRepoIndexesEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.RepoRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GithubRepositoryEventsIT extends IntegrationTest {
    final Long CAIRO_STREAMS_ID = 493795808L;
    @Autowired
    public RepoIndexingJobEntityRepository repoIndexingJobEntityRepository;
    @Autowired
    public JobManager diffRepoRefreshJobManager;
    @Autowired
    OldRepoIndexesEntityRepository oldRepoIndexesEntityRepository;
    @Autowired
    GithubRepoEntityRepository githubRepoRepository;
    @Autowired
    RepoRepository rawRepoStorage;

    @Test
    @Order(1)
    void init() {
        processEventsFromPaths("installation",
                "/github/webhook/events/installation/installation_created_old.json"
        );
    }

    @Test
    @Order(2)
    void should_handle_repo_becoming_private() {
        // When
        processEventsFromPaths("repository",
                "/github/webhook/events/repository/cairo-streams-privatized.json"
        );

        // Then
        assertThat(repoIndexingJobEntityRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getIsPublic()).isFalse();
        assertThat(oldRepoIndexesEntityRepository.findById(CAIRO_STREAMS_ID)).isEmpty();
        assertAllEventsAreProcessed("repository");
    }

    @Test
    @Order(3)
    void should_handle_repo_becoming_public() {
        // When
        processEventsFromPaths("repository",
                "/github/webhook/events/repository/cairo-streams-publicized.json"
        );

        // Then
        assertThat(repoIndexingJobEntityRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getIsPublic()).isTrue();
        assertThat(oldRepoIndexesEntityRepository.findById(CAIRO_STREAMS_ID)).isPresent();
        assertAllEventsAreProcessed("repository");
    }

    @Test
    @Order(4)
    void should_handle_repo_updated() {
        // When
        processEventsFromPaths("repository",
                "/github/webhook/events/repository/cairo-streams-edited.json"
        );
        assertAllEventsAreProcessed("repository");
    }

    @Test
    @Order(4)
    void should_handle_repo_deleted() {
        // Given
        diffRepoRefreshJobManager.createJob().run();

        // When
        processEventsFromPaths("repository",
                "/github/webhook/events/repository/cairo-streams-deleted.json"
        );

        // Then
        assertThat(repoIndexingJobEntityRepository.findById(CAIRO_STREAMS_ID)).isEmpty();
        assertThat(githubRepoRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getDeletedAt().toString()).isEqualTo("2023-12-05 08:02:21.0");
        assertThat(rawRepoStorage.findById(CAIRO_STREAMS_ID).orElseThrow().getDeleted()).isTrue();
    }
}
