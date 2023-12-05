package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubIssueEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.RepoContributorEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubIssueRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.RepoContributorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void should_handle_issue_events() {
        // When
        processEventsFromPaths("issues",
                "/github/webhook/events/issues/marketplace-frontend-issue-78-opened.json");

        // Then
        final var issue = githubIssueRepository.findById(ISSUE_ID).orElseThrow();
        assertThat(issue.getId()).isEqualTo(ISSUE_ID);
        assertThat(issue.getNumber()).isEqualTo(78);
        assertThat(issue.getTitle()).isEqualTo("handle github errors gracefully");
        assertThat(issue.getBody()).isEqualTo("when github returns 404/500/403/... ignore the error and enter best effort mode with not all of the data returned\n");
        assertThat(issue.getHtmlUrl()).isEqualTo("https://github.com/onlydustxyz/marketplace-frontend/issues/78");
        assertThat(issue.getStatus()).isEqualTo(GithubIssueEntity.Status.COMPLETED);
        assertThat(issue.getCreatedAt().toString()).isEqualTo("2022-07-12 09:55:06.0");
        assertThat(issue.getAuthor().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(issue.getRepo().getName()).isEqualTo("marketplace-frontend");
        assertThat(issue.getAssignees()).hasSize(1);
        assertThat(issue.getAssignees().get(0).getLogin()).isEqualTo("AnthonyBuisset");

        final var contributions = contributionRepository.findAll();
        assertThat(contributions).hasSize(1);
        assertThat(contributions.get(0).getIssue().getId()).isEqualTo(ISSUE_ID);
        assertThat(contributions.get(0).getContributor().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(contributions.get(0).getStatus()).isEqualTo(ContributionEntity.Status.COMPLETED);

        assertThat(repoContributorRepository.findAll()).containsExactlyInAnyOrder(
                new RepoContributorEntity(new RepoContributorEntity.Id(MARKETPLACE_FRONTEND_ID, ANTHONY_ID), 1, 1)
        );
    }
}
