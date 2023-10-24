package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanCodeReview;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubCodeReview;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawCodeReview;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageRepositoryStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubCodeReviewTest {
    final RawAccount anthony = RawStorageRepositoryStub.load("/github/users/anthony.json", RawAccount.class);
    final RawAccount pierre = RawStorageRepositoryStub.load("/github/users/pierre.json", RawAccount.class);
    final RawPullRequest pr1257 = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend/pulls/1257.json", RawPullRequest.class);
    final RawCodeReview[] pr1257Reviews = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend/pulls/1257_reviews.json", RawCodeReview[].class);

    @Test
    public void should_return_correct_id() {
        // WARNING: Modifying this test means that all existing ids in database must be updated
        final var reviewer = CleanAccount.of(pierre);
        final var pullRequest = CleanPullRequest.of(pr1257, CleanRepo.of(pr1257.getBase().getRepo(), CleanAccount.of(anthony)), CleanAccount.of(anthony));
        final var codeReview = CleanCodeReview.of(pr1257Reviews[0], reviewer);

        assertThat(GithubCodeReview.of(codeReview, pullRequest).getId()).isEqualTo("eada5d9c5fee512cf4c4a2d6af5125d6b5930f40ae84a59026d523ee8849e197");
    }
}
