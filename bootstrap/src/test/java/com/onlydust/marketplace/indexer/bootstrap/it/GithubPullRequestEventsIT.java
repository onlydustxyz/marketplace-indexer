package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubPullRequestEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.RepoContributorEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubPullRequestRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.RepoContributorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubPullRequestEventsIT extends IntegrationTest {
    final static Long PR_ID = 1524797398L;
    final static Long OFUX_ID = 595505L;
    final static Long ANTHONY_ID = 43467246L;
    final static Long PIERRE_ID = 16590657L;
    final static Long MARKETPLACE_FRONTEND_ID = 498695724L;

    @Autowired
    GithubPullRequestRepository githubPullRequestRepository;
    @Autowired
    ContributionRepository contributionRepository;
    @Autowired
    RepoContributorRepository repoContributorRepository;

    @Test
    void should_handle_pull_request_events() {
        // When
        processEventsFromPaths("pull_request",
                "/github/webhook/events/pull_request/marketplace-frontend-pr-1257-opened.json");

        // Then
        final var pullRequest = githubPullRequestRepository.findById(PR_ID).orElseThrow();
        assertThat(pullRequest.getId()).isEqualTo(PR_ID);
        assertThat(pullRequest.getNumber()).isEqualTo(1257);
        assertThat(pullRequest.getTitle()).isEqualTo("fix migration");
        assertThat(pullRequest.getBody()).isNull();
        assertThat(pullRequest.getHtmlUrl()).isEqualTo("https://github.com/onlydustxyz/marketplace-frontend/pull/1257");
        assertThat(pullRequest.getStatus()).isEqualTo(GithubPullRequestEntity.Status.MERGED);
        assertThat(pullRequest.getCreatedAt().toString()).isEqualTo("2023-09-21 12:42:45.0");
        assertThat(pullRequest.getAuthor().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(pullRequest.getRepo().getName()).isEqualTo("marketplace-frontend");
        assertThat(pullRequest.getCommitCount()).isEqualTo(1);
        assertThat(pullRequest.getReviewState()).isEqualTo(GithubPullRequestEntity.ReviewState.APPROVED);

        final var contributions = contributionRepository.findAll(Sort.by("type", "contributor.login"));
        assertThat(contributions).hasSize(4);
        assertThat(contributions.get(0).getGithubNumber()).isEqualTo(1257);
        assertThat(contributions.get(0).getType()).isEqualTo(ContributionEntity.Type.PULL_REQUEST);
        assertThat(contributions.get(0).getContributor().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(contributions.get(1).getGithubNumber()).isEqualTo(78);
        assertThat(contributions.get(1).getType()).isEqualTo(ContributionEntity.Type.ISSUE);
        assertThat(contributions.get(1).getContributor().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(contributions.get(2).getGithubNumber()).isEqualTo(1257);
        assertThat(contributions.get(2).getType()).isEqualTo(ContributionEntity.Type.CODE_REVIEW);
        assertThat(contributions.get(2).getContributor().getLogin()).isEqualTo("PierreOucif");
        assertThat(contributions.get(3).getGithubNumber()).isEqualTo(1257);
        assertThat(contributions.get(3).getType()).isEqualTo(ContributionEntity.Type.CODE_REVIEW);
        assertThat(contributions.get(3).getContributor().getLogin()).isEqualTo("ofux");

        assertThat(repoContributorRepository.findAll()).containsExactlyInAnyOrder(
                new RepoContributorEntity(new RepoContributorEntity.Id(MARKETPLACE_FRONTEND_ID, OFUX_ID), 0, 1),
                new RepoContributorEntity(new RepoContributorEntity.Id(MARKETPLACE_FRONTEND_ID, ANTHONY_ID), 2, 2),
                new RepoContributorEntity(new RepoContributorEntity.Id(MARKETPLACE_FRONTEND_ID, PIERRE_ID), 1, 1)
        );
    }
}
