package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanCodeReview;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubCodeReview;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubPullRequest;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawCodeReview;
import com.onlydust.marketplace.indexer.domain.models.raw.RawLanguages;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

public class ContributionTest {
    final RawAccount anthony = RawStorageWriterStub.load("/github/users/anthony.json", RawAccount.class);
    final RawAccount pierre = RawStorageWriterStub.load("/github/users/pierre.json", RawAccount.class);
    final RawPullRequest pr1257 = RawStorageWriterStub.load("/github/repos/marketplace-frontend/pulls/1257.json", RawPullRequest.class);
    final RawCodeReview[] pr1257Reviews = RawStorageWriterStub.load("/github/repos/marketplace-frontend/pulls/1257_reviews.json", RawCodeReview[].class);

    @Test
    public void should_return_correct_id_for_code_review() {
        // WARNING: Modifying this test means that all existing ids in database must be updated
        final var reviewer = CleanAccount.of(pierre);
        final var pullRequest = CleanPullRequest.of(pr1257, CleanRepo.of(pr1257.getBase().getRepo(), CleanAccount.of(anthony), new RawLanguages(), null), CleanAccount.of(anthony));
        final var codeReview = CleanCodeReview.of(pr1257Reviews[0], reviewer);

        final var contribution = GithubCodeReview.of(codeReview, pullRequest);

        assertThat(Contribution.of(contribution).getId()).isEqualTo("2a61afc3a96d1aa429bc3e3b4ae4e663c6440c17408230d0b1448eee77533815");
    }

    @Test
    public void should_return_correct_id_for_pull_request() {
        // WARNING: Modifying this test means that all existing ids in database must be updated
        final var pullRequest = CleanPullRequest.of(pr1257, CleanRepo.of(pr1257.getBase().getRepo(), CleanAccount.of(anthony), new RawLanguages(), null), CleanAccount.of(anthony));
        final var contribution = GithubPullRequest.of(pullRequest);

        assertThat(Contribution.of(contribution).getId()).isEqualTo("8aa3f462c9613831dc0dc72a1912a06772311d079595db73b9e13af2c3feb233");
    }

    @Test
    public void should_compute_code_review_state() {
        assertThat(Contribution.Status.of(GithubCodeReview.State.PENDING, GithubPullRequest.Status.OPEN)).isEqualTo(Contribution.Status.IN_PROGRESS);
        assertThat(Contribution.Status.of(GithubCodeReview.State.PENDING, GithubPullRequest.Status.DRAFT)).isEqualTo(Contribution.Status.IN_PROGRESS);
        assertThat(Contribution.Status.of(GithubCodeReview.State.PENDING, GithubPullRequest.Status.CLOSED)).isEqualTo(Contribution.Status.CANCELLED);
        assertThat(Contribution.Status.of(GithubCodeReview.State.PENDING, GithubPullRequest.Status.MERGED)).isEqualTo(Contribution.Status.CANCELLED);

        assertThat(Contribution.Status.of(GithubCodeReview.State.COMMENTED, GithubPullRequest.Status.OPEN)).isEqualTo(Contribution.Status.IN_PROGRESS);
        assertThat(Contribution.Status.of(GithubCodeReview.State.COMMENTED, GithubPullRequest.Status.DRAFT)).isEqualTo(Contribution.Status.IN_PROGRESS);
        assertThat(Contribution.Status.of(GithubCodeReview.State.COMMENTED, GithubPullRequest.Status.CLOSED)).isEqualTo(Contribution.Status.CANCELLED);
        assertThat(Contribution.Status.of(GithubCodeReview.State.COMMENTED, GithubPullRequest.Status.MERGED)).isEqualTo(Contribution.Status.CANCELLED);

        assertThat(Contribution.Status.of(GithubCodeReview.State.DISMISSED, GithubPullRequest.Status.OPEN)).isEqualTo(Contribution.Status.CANCELLED);
        assertThat(Contribution.Status.of(GithubCodeReview.State.DISMISSED, GithubPullRequest.Status.DRAFT)).isEqualTo(Contribution.Status.CANCELLED);
        assertThat(Contribution.Status.of(GithubCodeReview.State.DISMISSED, GithubPullRequest.Status.CLOSED)).isEqualTo(Contribution.Status.CANCELLED);
        assertThat(Contribution.Status.of(GithubCodeReview.State.DISMISSED, GithubPullRequest.Status.MERGED)).isEqualTo(Contribution.Status.CANCELLED);

        assertThat(Contribution.Status.of(GithubCodeReview.State.APPROVED, GithubPullRequest.Status.OPEN)).isEqualTo(Contribution.Status.COMPLETED);
        assertThat(Contribution.Status.of(GithubCodeReview.State.APPROVED, GithubPullRequest.Status.DRAFT)).isEqualTo(Contribution.Status.COMPLETED);
        assertThat(Contribution.Status.of(GithubCodeReview.State.APPROVED, GithubPullRequest.Status.CLOSED)).isEqualTo(Contribution.Status.COMPLETED);
        assertThat(Contribution.Status.of(GithubCodeReview.State.APPROVED, GithubPullRequest.Status.MERGED)).isEqualTo(Contribution.Status.COMPLETED);

        assertThat(Contribution.Status.of(GithubCodeReview.State.CHANGES_REQUESTED, GithubPullRequest.Status.OPEN)).isEqualTo(Contribution.Status.COMPLETED);
        assertThat(Contribution.Status.of(GithubCodeReview.State.CHANGES_REQUESTED, GithubPullRequest.Status.DRAFT)).isEqualTo(Contribution.Status.COMPLETED);
        assertThat(Contribution.Status.of(GithubCodeReview.State.CHANGES_REQUESTED, GithubPullRequest.Status.CLOSED)).isEqualTo(Contribution.Status.COMPLETED);
        assertThat(Contribution.Status.of(GithubCodeReview.State.CHANGES_REQUESTED, GithubPullRequest.Status.MERGED)).isEqualTo(Contribution.Status.COMPLETED);
    }

    @Test
    public void should_compute_code_review_completion_date() {
        final var codeReviewSubmissionDate = Date.from(Instant.now().minusSeconds(30));
        final var pullRequestClosedDate = Date.from(Instant.now().minusSeconds(15));
        final var pullRequestMergedDate = Date.from(Instant.now().minusSeconds(10));

        final BiFunction<GithubCodeReview.State, GithubPullRequest.Status, Contribution> contribution =
                (GithubCodeReview.State codeReviewState, GithubPullRequest.Status pullRequestStatus) ->
                        Contribution.of(GithubCodeReview.builder()
                                .submittedAt(codeReviewSubmissionDate)
                                .state(codeReviewState)
                                .pullRequest(GithubPullRequest.builder()
                                        .status(pullRequestStatus)
                                        .mergedAt(pullRequestMergedDate)
                                        .closedAt(pullRequestClosedDate)
                                        .build())
                                .build());

        assertThat(contribution.apply(GithubCodeReview.State.PENDING, GithubPullRequest.Status.OPEN).getCompletedAt()).isNull();
        assertThat(contribution.apply(GithubCodeReview.State.PENDING, GithubPullRequest.Status.DRAFT).getCompletedAt()).isNull();
        assertThat(contribution.apply(GithubCodeReview.State.PENDING, GithubPullRequest.Status.CLOSED).getCompletedAt()).isEqualTo(pullRequestClosedDate);
        assertThat(contribution.apply(GithubCodeReview.State.PENDING, GithubPullRequest.Status.MERGED).getCompletedAt()).isEqualTo(pullRequestMergedDate);

        assertThat(contribution.apply(GithubCodeReview.State.COMMENTED, GithubPullRequest.Status.OPEN).getCompletedAt()).isNull();
        assertThat(contribution.apply(GithubCodeReview.State.COMMENTED, GithubPullRequest.Status.DRAFT).getCompletedAt()).isNull();
        assertThat(contribution.apply(GithubCodeReview.State.COMMENTED, GithubPullRequest.Status.CLOSED).getCompletedAt()).isEqualTo(pullRequestClosedDate);
        assertThat(contribution.apply(GithubCodeReview.State.COMMENTED, GithubPullRequest.Status.MERGED).getCompletedAt()).isEqualTo(pullRequestMergedDate);

        assertThat(contribution.apply(GithubCodeReview.State.DISMISSED, GithubPullRequest.Status.OPEN).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);
        assertThat(contribution.apply(GithubCodeReview.State.DISMISSED, GithubPullRequest.Status.DRAFT).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);
        assertThat(contribution.apply(GithubCodeReview.State.DISMISSED, GithubPullRequest.Status.CLOSED).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);
        assertThat(contribution.apply(GithubCodeReview.State.DISMISSED, GithubPullRequest.Status.MERGED).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);

        assertThat(contribution.apply(GithubCodeReview.State.APPROVED, GithubPullRequest.Status.OPEN).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);
        assertThat(contribution.apply(GithubCodeReview.State.APPROVED, GithubPullRequest.Status.DRAFT).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);
        assertThat(contribution.apply(GithubCodeReview.State.APPROVED, GithubPullRequest.Status.CLOSED).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);
        assertThat(contribution.apply(GithubCodeReview.State.APPROVED, GithubPullRequest.Status.MERGED).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);

        assertThat(contribution.apply(GithubCodeReview.State.CHANGES_REQUESTED, GithubPullRequest.Status.OPEN).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);
        assertThat(contribution.apply(GithubCodeReview.State.CHANGES_REQUESTED, GithubPullRequest.Status.DRAFT).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);
        assertThat(contribution.apply(GithubCodeReview.State.CHANGES_REQUESTED, GithubPullRequest.Status.CLOSED).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);
        assertThat(contribution.apply(GithubCodeReview.State.CHANGES_REQUESTED, GithubPullRequest.Status.MERGED).getCompletedAt()).isEqualTo(codeReviewSubmissionDate);
    }
}
