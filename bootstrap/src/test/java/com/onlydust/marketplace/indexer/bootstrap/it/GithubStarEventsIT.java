package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GithubStarEventsIT extends IntegrationTest {
    final Long CAIRO_STREAMS_ID = 493795808L;
    @Autowired
    GithubRepoEntityRepository githubRepoRepository;

    @Test
    @Order(1)
    void init() {
        processEventsFromPaths("installation",
                "/github/webhook/events/installation/installation_created_old.json"
        );
    }

    @Test
    @Order(2)
    void should_handle_star_events() {
        // When
        processEventsFromPaths("star",
                "/github/webhook/events/star/cairo-streams-starred.json"
        );

        // Then
        assertThat(githubRepoRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getStarsCount()).isEqualTo(60);
    }
}
