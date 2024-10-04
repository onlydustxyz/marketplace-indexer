package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.bootstrap.it.stubs.PublicEventRawStorageReaderStub;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.repositories.UserPublicEventsIndexingJobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class UserPublicEventIndexingIT extends IntegrationTest {
    private final static Long ANTHONY = 43467246L;

    @Autowired
    public UserPublicEventsIndexingJobRepository userPublicEventsIndexingJobRepository;

    @Autowired
    PublicEventRawStorageReaderStub githubArchivesReaderStub;
    PublicEventRawStorageReaderStub githubApiReaderStub;

    private WebTestClient.ResponseSpec indexUser(Long userId) {
        return put("/api/v1/users/" + userId);
    }

    @Test
    public void should_index_user_from_public_events() {
        // Given
        githubArchivesReaderStub.add("/github/public_events/antho_20231002.json");
        githubArchivesReaderStub.add("/github/public_events/antho_20231003.json");
        githubApiReaderStub.add("/github/public_events/antho_last_events.json");

        // When
        indexUser(ANTHONY).expectStatus().isNoContent();

        // Then
        final var jobs = userPublicEventsIndexingJobRepository.findAll();
        assertThat(jobs)
                .hasSize(1)
                .allMatch(job -> nonNull(job.startedAt()) && nonNull(job.finishedAt()) && job.status() == JobStatus.SUCCESS);
    }
}
