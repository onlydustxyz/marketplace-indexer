package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.bootstrap.it.stubs.PublicEventRawStorageReaderStub;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.UserPublicEventsIndexingJobRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.onlydust.marketplace.indexer.bootstrap.it.helpers.DateHelper.at;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class UserPublicEventIndexingIT extends IntegrationTest {
    private final static Long ANTHONY = 43467246L;

    @Autowired
    UserPublicEventsIndexingJobRepository userPublicEventsIndexingJobRepository;
    @Autowired
    ContributionRepository contributionRepository;
    @Autowired
    GithubRepoEntityRepository repoRepository;
    @Autowired
    PublicEventRawStorageReaderStub githubArchivesReaderStub;
    @Autowired
    PublicEventRawStorageReaderStub githubApiReaderStub;
    @Autowired
    JobManager commitIndexerJobManager;

    private WebTestClient.ResponseSpec indexUser(Long userId) {
        return put("/api/v1/users/" + userId);
    }

    @Test
    @Transactional
    public void should_index_user_from_public_events() {
        // Given
        githubArchivesReaderStub.add("/github/public_events/antho_20231002.json");
        githubApiReaderStub.add("/github/public_events/antho_last_events.json");

        // When
        at("2024-10-03T17:00:00Z", () -> indexUser(ANTHONY).expectStatus().isNoContent());

        // Then
        assertThat(userPublicEventsIndexingJobRepository.findAll())
                .hasSize(1)
                .allMatch(job -> nonNull(job.startedAt()) && nonNull(job.finishedAt()) && job.status() == JobStatus.SUCCESS);

        assertThat(contributionRepository.findAll())
                .hasSize(6)
                .extracting(ContributionEntity::getContributorLogin)
                .allMatch(l -> l.equals("AnthonyBuisset"));

        assertThat(repoRepository.findAll())
                .hasSize(1)
                .allMatch(r -> r.getName().equals("marketplace-api") && !r.getLanguages().isEmpty());
    }

    @Test
    @Transactional
    public void should_index_user_languages_from_public_events() {
        // Given
        githubArchivesReaderStub.add("/github/public_events/antho_20231002.json");
        githubApiReaderStub.add("/github/public_events/antho_last_events.json");

        // When
        at("2024-10-03T17:00:00Z", () -> indexUser(ANTHONY).expectStatus().isNoContent());

        // Then
        assertThat(userPublicEventsIndexingJobRepository.findAll())
                .hasSize(1)
                .allMatch(job -> nonNull(job.startedAt()) && nonNull(job.finishedAt()) && job.status() == JobStatus.SUCCESS);

        commitIndexerJobManager.createJob().run();
    }
}
