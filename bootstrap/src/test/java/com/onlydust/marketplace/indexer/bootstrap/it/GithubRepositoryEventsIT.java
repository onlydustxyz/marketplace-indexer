package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.RepoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GithubRepositoryEventsIT extends IntegrationTest {
    final Long CAIRO_STREAMS_ID = 493795808L;
    @Autowired
    public RepoIndexingJobEntityRepository repoIndexingJobEntityRepository;
    @Autowired
    GithubRepoRepository githubRepoRepository;
    @Autowired
    RepoRepository rawRepoStorage;

    @BeforeEach
    void setUp() {
        githubWireMockServer.resetAll();
    }

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
        assertThat(repoIndexingJobEntityRepository.findById(CAIRO_STREAMS_ID).orElseThrow().isPublic()).isFalse();
        assertAllEventsAreProcessed("repository");
        githubWireMockServer.verify(0, getRequestedFor(urlEqualTo("/repos/onlydust/cairo-streams")));
        githubWireMockServer.verify(0, getRequestedFor(urlEqualTo("/repositories/" + CAIRO_STREAMS_ID)));
    }

    @Test
    @Order(3)
    void should_handle_repo_becoming_public() {
        // When
        processEventsFromPaths("repository",
                "/github/webhook/events/repository/cairo-streams-publicized.json"
        );

        // Then
        assertThat(repoIndexingJobEntityRepository.findById(CAIRO_STREAMS_ID).orElseThrow().isPublic()).isTrue();
        assertAllEventsAreProcessed("repository");
        githubWireMockServer.verify(0, getRequestedFor(urlEqualTo("/repos/onlydust/cairo-streams")));
        githubWireMockServer.verify(0, getRequestedFor(urlEqualTo("/repositories/" + CAIRO_STREAMS_ID)));
    }

    @Test
    @Order(4)
    void should_handle_repo_updated() {
        // When
        processEventsFromPaths("repository",
                "/github/webhook/events/repository/cairo-streams-edited.json"
        );
        assertAllEventsAreProcessed("repository");
        githubWireMockServer.verify(0, getRequestedFor(urlEqualTo("/repos/onlydust/cairo-streams")));
        githubWireMockServer.verify(0, getRequestedFor(urlEqualTo("/repositories/" + CAIRO_STREAMS_ID)));
    }

    @Test
    @Order(4)
    void should_handle_repo_deleted() {
        // When
        processEventsFromPaths("repository",
                "/github/webhook/events/repository/cairo-streams-deleted.json"
        );

        // Then
        assertThat(repoIndexingJobEntityRepository.findById(CAIRO_STREAMS_ID)).isEmpty();
        assertThat(githubRepoRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getDeletedAt().toString()).isEqualTo("2023-12-05 08:02:21.0");
        assertThat(rawRepoStorage.findById(CAIRO_STREAMS_ID).orElseThrow().deleted()).isTrue();

        githubWireMockServer.verify(0, getRequestedFor(urlEqualTo("/repos/onlydust/cairo-streams")));
        githubWireMockServer.verify(0, getRequestedFor(urlEqualTo("/repositories/" + CAIRO_STREAMS_ID)));
    }
}
