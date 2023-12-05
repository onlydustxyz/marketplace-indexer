package com.onlydust.marketplace.indexer.bootstrap.it;

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
public class GithubStarEventsIT extends IntegrationTest {
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
    void should_handle_star_events() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/star/cairo-streams-starred.json").toURI()));

        // When
        processEvent(event, "star");

        // Then
        assertThat(githubRepoRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getStarsCount()).isEqualTo(60);
    }
}
