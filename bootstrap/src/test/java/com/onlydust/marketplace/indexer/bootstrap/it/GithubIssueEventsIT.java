package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubIssueEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubIssueRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.RepoContributorRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GithubIssueEventsIT extends IntegrationTest {
    final static Long ISSUE_ID = 2025877285L;
    final static Long OFUX_ID = 595505L;
    final static Long PIERRE_ID = 16590657L;
    final static Long CAIRO_STREAMS_ID = 493795808L;

    @Autowired
    GithubIssueRepository githubIssueRepository;
    @Autowired
    ContributionRepository contributionRepository;
    @Autowired
    RepoContributorRepository repoContributorRepository;

    @Test
    @Order(1)
    void init() {
        processEventsFromPaths("installation",
                "/github/webhook/events/installation/installation_created_old.json");
    }

    @Test
    @Order(2)
    void should_handle_issue_being_created() {
        // When
        processEventsFromPaths("issues",
                "/github/webhook/events/issues/cairo-streams-issue-29-opened.json");

        // Then
        final var issue = githubIssueRepository.findById(ISSUE_ID).orElseThrow();
        assertThat(issue.getId()).isEqualTo(ISSUE_ID);
        assertThat(issue.getNumber()).isEqualTo(29);
        assertThat(issue.getTitle()).isEqualTo("New issue created");
        assertThat(issue.getBody()).isEqualTo("This issue is created for testing purposes");
        assertThat(issue.getHtmlUrl()).isEqualTo("https://github.com/onlydustxyz/cairo-streams/issues/29");
        assertThat(issue.getStatus()).isEqualTo(GithubIssueEntity.Status.OPEN);
        assertThat(issue.getCreatedAt().toString()).isEqualTo("2023-12-05 10:38:44.0");
        assertThat(issue.getAuthor().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(issue.getRepo().getName()).isEqualTo("cairo-streams");
        assertThat(issue.getAssignees()).hasSize(1);
        assertThat(issue.getAssignees().get(0).getLogin()).isEqualTo("ofux");

        final var contributions = contributionRepository.findAll();
        assertThat(contributions).hasSize(1);
        assertThat(contributions.get(0).getIssue().getId()).isEqualTo(ISSUE_ID);
        assertThat(contributions.get(0).getContributor().getLogin()).isEqualTo("ofux");

        final var repoContributors = repoContributorRepository.findAll();
        assertThat(repoContributors).hasSize(1);
        assertThat(repoContributors.get(0).getId().getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(repoContributors.get(0).getId().getContributorId()).isEqualTo(OFUX_ID);
    }
    
    @Test
    @Order(3)
    void should_handle_issue_being_modified() {
        // When
        processEventsFromPaths("issues",
                "/github/webhook/events/issues/cairo-streams-issue-29-assigned.json",
                "/github/webhook/events/issues/cairo-streams-issue-29-unassigned.json"
        );

        // Then
        final var issue = githubIssueRepository.findById(ISSUE_ID).orElseThrow();
        assertThat(issue.getAssignees()).hasSize(1);
        assertThat(issue.getAssignees().get(0).getLogin()).isEqualTo("PierreOucif");

        final var contributions = contributionRepository.findAll();
        assertThat(contributions).hasSize(1);
        assertThat(contributions.get(0).getIssue().getId()).isEqualTo(ISSUE_ID);
        assertThat(contributions.get(0).getContributor().getLogin()).isEqualTo("PierreOucif");

        final var repoContributors = repoContributorRepository.findAll();
        assertThat(repoContributors).hasSize(1);
        assertThat(repoContributors.get(0).getId().getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(repoContributors.get(0).getId().getContributorId()).isEqualTo(PIERRE_ID);
    }
}
