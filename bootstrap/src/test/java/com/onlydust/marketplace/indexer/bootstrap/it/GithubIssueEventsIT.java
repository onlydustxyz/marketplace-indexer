package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubIssueEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubLabelEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.RepoContributorEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubIssueAssigneeRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubIssueRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.RepoContributorRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GithubIssueEventsIT extends IntegrationTest {
    final static Long ISSUE_ID = 1301824165L;
    final static Long ANTHONY_ID = 43467246L;
    final static Long MARKETPLACE_FRONTEND_ID = 498695724L;

    @Autowired
    GithubIssueRepository githubIssueRepository;
    @Autowired
    ContributionRepository contributionRepository;
    @Autowired
    RepoContributorRepository repoContributorRepository;
    @Autowired
    GithubIssueAssigneeRepository githubIssueAssigneeRepository;

    @BeforeEach
    void setUp() {
        githubWireMockServer.resetAll();
    }

    @AfterEach
    void tearDown() {
        // verify there was no interaction with the GitHub API
        githubWireMockServer.findRequestsMatching(getRequestedFor(urlPathMatching(".*/issues/78")).build())
                .getRequests().forEach(System.out::println);
        githubWireMockServer.verify(0, getRequestedFor(urlPathMatching(".*/issues/78")));
    }

    @Test
    @Order(1)
    @Transactional
    void should_handle_issue_opened() {
        // When
        processEventsFromPaths("issues",
                "/github/webhook/events/issues/marketplace-frontend-issue-78-opened.json");

        // Then
        final var issue = githubIssueRepository.findById(ISSUE_ID).orElseThrow();
        assertThat(issue.getId()).isEqualTo(ISSUE_ID);
        assertThat(issue.getNumber()).isEqualTo(78);
        assertThat(issue.getTitle()).isEqualTo("handle github errors gracefully");
        assertThat(issue.getBody()).isEqualTo(
                "when github returns 404/500/403/... ignore the error and enter best effort mode with not all of the data returned\n");
        assertThat(issue.getHtmlUrl()).isEqualTo("https://github.com/onlydustxyz/marketplace-frontend/issues/78");
        assertThat(issue.getStatus()).isEqualTo(GithubIssueEntity.Status.COMPLETED);
        assertThat(issue.getCreatedAt()).isEqualTo("2022-07-12T09:55:06.000");
        assertThat(issue.getAuthor().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(issue.getRepo().getName()).isEqualTo("marketplace-frontend");
        assertThat(issue.getLabels()).hasSize(6);

        final var labels = issue.getLabels().stream().sorted(comparing(GithubLabelEntity::getId)).toList();
        assertThat(labels.get(0).getId()).isEqualTo(4204558788L);
        assertThat(labels.get(0).getName()).isEqualTo("Duration: under a day");
        assertThat(labels.get(0).getDescription()).isEqualTo("wil take up to one day");
        assertThat(labels.get(1).getId()).isEqualTo(4204558791L);
        assertThat(labels.get(1).getName()).isEqualTo("Difficulty: easy");
        assertThat(labels.get(1).getDescription()).isEqualTo("anybody can understand it");
        assertThat(labels.get(2).getId()).isEqualTo(4204558792L);
        assertThat(labels.get(2).getName()).isEqualTo("Context: isolated");
        assertThat(labels.get(2).getDescription()).isEqualTo("no previous knowledge of the codebase required");
        assertThat(labels.get(3).getId()).isEqualTo(4204558796L);
        assertThat(labels.get(3).getName()).isEqualTo("Type: feature");
        assertThat(labels.get(3).getDescription()).isEqualTo("a new feature to implement");
        assertThat(labels.get(4).getId()).isEqualTo(4204558798L);
        assertThat(labels.get(4).getName()).isEqualTo("State: open");
        assertThat(labels.get(4).getDescription()).isEqualTo("ready for contribution");
        assertThat(labels.get(5).getId()).isEqualTo(4204558800L);
        assertThat(labels.get(5).getName()).isEqualTo("Techno: rust");
        assertThat(labels.get(5).getDescription()).isEqualTo("rust");

        final var contributions = contributionRepository.findAll();
        assertThat(contributions).hasSize(1);
        assertThat(contributions.get(0).getIssue().getId()).isEqualTo(ISSUE_ID);
        assertThat(contributions.get(0).getContributor().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(contributions.get(0).getStatus()).isEqualTo(ContributionEntity.Status.COMPLETED);

        assertThat(repoContributorRepository.findAll()).containsExactlyInAnyOrder(
                new RepoContributorEntity(new RepoContributorEntity.Id(MARKETPLACE_FRONTEND_ID, ANTHONY_ID), 1, 1)
        );

        assertThat(githubIssueAssigneeRepository.findAllByIssueId(ISSUE_ID)).isEmpty();
    }


    @Test
    @Order(2)
    @Transactional
    void should_handle_issue_assigned() {
        // When
        processEventsFromPaths("issues",
                "/github/webhook/events/issues/marketplace-frontend-issue-78-assigned.json");

        // Then
        final var assignees = githubIssueAssigneeRepository.findAllByIssueId(ISSUE_ID);
        assertThat(assignees).hasSize(1);
        assertThat(assignees.get(0).getUser().getId()).isEqualTo(43467246L);
        assertThat(assignees.get(0).getAssignedByUser().getId()).isEqualTo(595505L);
    }

    @Test
    @Order(3)
    @Transactional
    void should_handle_issue_unassigned() {
        // When
        processEventsFromPaths("issues",
                "/github/webhook/events/issues/marketplace-frontend-issue-78-unassigned.json");

        // Then
        assertThat(githubIssueAssigneeRepository.findAllByIssueId(ISSUE_ID)).isEmpty();
    }

    @Test
    @Order(4)
    @Transactional
    void should_handle_issue_transferred() {
        // When
        processEventsFromPaths("issues",
                "/github/webhook/events/issues/marketplace-frontend-issue-78-assigned.json", // Make sure issue has assignees
                "/github/webhook/events/issues/marketplace-frontend-issue-78-transferred.json");

        // Then
        assertThat(githubIssueRepository.findById(ISSUE_ID)).isEmpty();
        assertThat(githubIssueAssigneeRepository.findAllByIssueId(ISSUE_ID)).isEmpty();
    }
}
