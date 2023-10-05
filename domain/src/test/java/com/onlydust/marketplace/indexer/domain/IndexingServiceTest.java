package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import com.onlydust.marketplace.indexer.domain.model.raw.RawCodeReview;
import com.onlydust.marketplace.indexer.domain.model.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.model.raw.RawUser;
import com.onlydust.marketplace.indexer.domain.ports.out.CacheWriteRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IndexingServiceTest {
    final RawUser anthony = RawStorageRepositoryStub.load("/github/users/anthony.json", RawUser.class);
    final RawUser pierre = RawStorageRepositoryStub.load("/github/users/pierre.json", RawUser.class);
    final RawSocialAccount[] anthonySocialAccounts = RawStorageRepositoryStub.load("/github/users/anthony_social_accounts.json", RawSocialAccount[].class);
    final RawSocialAccount[] pierreSocialAccounts = RawStorageRepositoryStub.load("/github/users/anthony_social_accounts.json", RawSocialAccount[].class);
    final RawPullRequest pr1257 = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend/pulls/1257.json", RawPullRequest.class);
    final RawCodeReview[] pr1257Reviews = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend/pulls/1257_reviews.json", RawCodeReview[].class);
    final RawStorageRepositoryStub rawStorageReader = new RawStorageRepositoryStub();
    final RawStorageRepositoryStub rawStorageRepository = new RawStorageRepositoryStub();
    final IndexingService indexer = new IndexingService(new CacheWriteRawStorageReaderDecorator(rawStorageReader, rawStorageRepository));

    @BeforeEach
    void setup() throws IOException {
        rawStorageReader.feedWith(anthony, pierre);
        rawStorageReader.feedWith(anthony.getId(), anthonySocialAccounts);
        rawStorageReader.feedWith(pierre.getId(), pierreSocialAccounts);
        rawStorageReader.feedWith(pr1257);
        rawStorageReader.feedWith(pr1257.getId(), pr1257Reviews);
    }

    @Test
    void should_index_user_from_its_id() {
        final var user = indexer.indexUser(anthony.getId());

        assertThat(user.id()).isEqualTo(anthony.getId());
        assertThat(user.login()).isEqualTo(anthony.getLogin());
        assertThat(user.socialAccounts()).containsExactly(anthonySocialAccounts);

        assertCachedUsersAre(anthony);
        assertCachedUserSocialAccountsAre(Map.entry(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList()));
    }


    @Test
    void should_index_pull_request() {
        final var pullRequest = indexer.indexPullRequest("onlydustxyz", "marketplace-frontend", 1257);

        assertThat(pullRequest.id()).isEqualTo(1524797398);
        assertThat(pullRequest.author().login()).isEqualTo("AnthonyBuisset");
        assertThat(pullRequest.reviews().get(0).id()).isEqualTo(pr1257Reviews[0].getId());
        assertThat(pullRequest.reviews().get(0).author().login()).isEqualTo("PierreOucif");
        assertThat(pullRequest.author().login()).isEqualTo("AnthonyBuisset");

        assertCachedPullRequestsAre(pr1257);
        assertCachedCodeReviewsAre(Map.entry(pr1257.getId(), Arrays.stream(pr1257Reviews).toList()));
        assertCachedUsersAre(anthony, pierre);
        assertCachedUserSocialAccountsAre(
                Map.entry(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList()),
                Map.entry(pierre.getId(), Arrays.stream(pierreSocialAccounts).toList())
        );
    }

    @Test
    void should_throw_when_indexing_non_existing_items() {
        assertThatThrownBy(() -> {
            indexer.indexUser(0);
        }).isInstanceOf(NotFound.class)
                .hasMessageContaining("User not found");

        assertCachedUsersAre();
    }

    private void assertCachedUsersAre(RawUser... rawUsers) {
        assertThat(rawStorageRepository.users()).containsExactly(rawUsers);
    }

    private void assertCachedUserSocialAccountsAre(Map.Entry<Integer, List<RawSocialAccount>>... entries) {
        assertThat(rawStorageRepository.userSocialAccounts()).containsExactly(entries);
    }

    private void assertCachedPullRequestsAre(RawPullRequest... pullRequests) {
        assertThat(rawStorageRepository.pullRequests()).containsExactly(pullRequests);
    }

    private void assertCachedCodeReviewsAre(Map.Entry<Integer, List<RawCodeReview>>... entries) {
        assertThat(rawStorageRepository.codeReviews()).containsExactly(entries);
    }

}
