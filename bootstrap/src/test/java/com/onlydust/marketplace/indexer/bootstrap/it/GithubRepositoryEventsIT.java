package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.OldRepoIndexesEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GithubRepositoryEventsIT extends IntegrationTest {
    final Long CAIRO_STREAMS_ID = 493795808L;
    @Autowired
    public RepoIndexingJobEntityRepository repoIndexingJobEntityRepository;
    @Autowired
    OldRepoIndexesEntityRepository oldRepoIndexesEntityRepository;
    @Autowired
    GithubRepoEntityRepository githubRepoRepository;

    @Test
    @Order(1)
    void init() throws URISyntaxException, IOException {
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation/installation_created_old.json").toURI()));
        processEvent(event, "installation");
    }

    @Test
    @Order(2)
    void should_handle_repo_becoming_private() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/repository/cairo-streams-privatized.json").toURI()));

        // When
        processEvent(event, "repository");

        // Then
        assertThat(repoIndexingJobEntityRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getIsPublic()).isFalse();
        assertThat(oldRepoIndexesEntityRepository.findById(CAIRO_STREAMS_ID)).isEmpty();
        assertThat(githubRepoRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getVisibility()).isEqualTo(GithubRepoEntity.Visibility.PRIVATE);
    }

    @Test
    @Order(3)
    void should_handle_repo_becoming_public() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/repository/cairo-streams-publicized.json").toURI()));

        // When
        processEvent(event, "repository");

        // Then
        assertThat(repoIndexingJobEntityRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getIsPublic()).isTrue();
        assertThat(oldRepoIndexesEntityRepository.findById(CAIRO_STREAMS_ID)).isPresent();
        assertThat(githubRepoRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getVisibility()).isEqualTo(GithubRepoEntity.Visibility.PUBLIC);
    }

    @Test
    @Order(4)
    void should_handle_repo_updated() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/repository/cairo-streams-edited.json").toURI()));

        // When
        processEvent(event, "repository");

        // Then
        assertThat(githubRepoRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getDescription()).isEqualTo("Array stream library written in old-fashioned Cairo");
    }
}
