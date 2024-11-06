package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.bootstrap.it.stubs.PublicEventRawStorageReaderStub;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubUserFileExtensionEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.UserPublicEventsIndexingJobRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubUserFileExtensionsRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZonedDateTime;

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
    GithubRepoRepository repoRepository;
    @Autowired
    PublicEventRawStorageReader publicEventRawStorageReader;
    PublicEventRawStorageReaderStub publicEventRawStorageReaderStub;
    @Autowired
    JobManager commitIndexerJobManager;
    @Autowired
    GithubUserFileExtensionsRepository githubUserFileExtensionsRepository;

    private WebTestClient.ResponseSpec indexUser(Long userId) {
        return put("/api/v1/users/" + userId);
    }

    @BeforeEach
    public void setUp() {
        publicEventRawStorageReaderStub = (PublicEventRawStorageReaderStub) publicEventRawStorageReader;
    }

    @Test
    @Transactional
    public void should_index_user_from_public_events() {
        // Given
        publicEventRawStorageReaderStub.add("/github/public_events/antho_20231002.json");

        // When
        at("2024-10-03T17:00:00Z", () -> indexUser(ANTHONY).expectStatus().isNoContent());

        // Then
        assertThat(userPublicEventsIndexingJobRepository.findAll())
                .hasSize(1)
                .allMatch(job -> nonNull(job.startedAt()) && nonNull(job.finishedAt()) && job.status() == JobStatus.SUCCESS && job.lastEventTimestamp().equals(ZonedDateTime.parse("2024-10-03T16:00:29Z")));

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
        publicEventRawStorageReaderStub.add("/github/public_events/antho_20231002.json");

        // When
        at("2024-10-03T17:00:00Z", () -> indexUser(ANTHONY).expectStatus().isNoContent());

        // Then
        assertThat(userPublicEventsIndexingJobRepository.findAll())
                .hasSize(1)
                .allMatch(job -> nonNull(job.startedAt()) && nonNull(job.finishedAt()) && job.status() == JobStatus.SUCCESS);

        commitIndexerJobManager.createJob().run();

        assertThat(githubUserFileExtensionsRepository.findAll().stream()
                .filter(e -> e.getUserId().equals(ANTHONY)))
                .hasSize(2);

        assertStats(ANTHONY, "java", 3, 9, 149);
        assertStats(ANTHONY, "sql", 2, 3, 212);
    }

    private void assertStats(Long userId, String fileExtension, int commitCount, int fileCount, int modificationCount) {
        final var stats = githubUserFileExtensionsRepository.findById(new GithubUserFileExtensionEntity.PrimaryKey(userId, fileExtension))
                .orElseThrow(() -> new AssertionError("No entity found for user " + userId + " and file extension " + fileExtension));

        assertThat(stats.getCommitCount()).isEqualTo(commitCount);
        assertThat(stats.getFileCount()).isEqualTo(fileCount);
        assertThat(stats.getModificationCount()).isEqualTo(modificationCount);
    }
}
