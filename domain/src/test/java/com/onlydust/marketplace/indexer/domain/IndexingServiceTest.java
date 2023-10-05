package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.out.CacheWriteRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageRepositoryStub;
import org.assertj.core.groups.Tuple;
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
    final RawUser olivier = RawStorageRepositoryStub.load("/github/users/olivier.json", RawUser.class);
    final RawSocialAccount[] anthonySocialAccounts = RawStorageRepositoryStub.load("/github/users/anthony_social_accounts.json", RawSocialAccount[].class);
    final RawSocialAccount[] pierreSocialAccounts = RawStorageRepositoryStub.load("/github/users/pierre_social_accounts.json", RawSocialAccount[].class);
    final RawSocialAccount[] olivierSocialAccounts = RawStorageRepositoryStub.load("/github/users/olivier_social_accounts.json", RawSocialAccount[].class);
    final RawPullRequest pr1257 = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend/pulls/1257.json", RawPullRequest.class);
    final RawIssue issue78 = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend/issues/78.json", RawIssue.class);
    final RawCodeReview[] pr1257Reviews = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend/pulls/1257_reviews.json", RawCodeReview[].class);
    final RawCommit[] pr1257Commits = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend/pulls/1257_commits.json", RawCommit[].class);
    final RawCheckRuns pr1257CheckRuns = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend/pulls/1257_check_runs.json", RawCheckRuns.class);
    final RawStorageRepositoryStub rawStorageReader = new RawStorageRepositoryStub();
    final RawStorageRepositoryStub rawStorageRepository = new RawStorageRepositoryStub();
    final IndexingService indexer = new IndexingService(new CacheWriteRawStorageReaderDecorator(rawStorageReader, rawStorageRepository));

    @BeforeEach
    void setup() throws IOException {
        rawStorageReader.feedWith(anthony, pierre, olivier);
        rawStorageReader.feedWith(anthony.getId(), anthonySocialAccounts);
        rawStorageReader.feedWith(pierre.getId(), pierreSocialAccounts);
        rawStorageReader.feedWith(olivier.getId(), olivierSocialAccounts);
        rawStorageReader.feedWith(pr1257);
        rawStorageReader.feedWith(issue78);
        rawStorageReader.feedWith(pr1257.getId(), pr1257Reviews);
        rawStorageReader.feedWith(pr1257.getId(), pr1257Commits);
        rawStorageReader.feedWith(pr1257.getHead().getRepo().getId(), pr1257.getHead().getSha(), pr1257CheckRuns);
    }

    @Test
    void should_index_user_from_its_id() {
        final var user = indexer.indexUser(anthony.getId());

        assertThat(user.id()).isEqualTo(anthony.getId());
        assertThat(user.login()).isEqualTo(anthony.getLogin());
        assertThat(user.socialAccounts()).containsExactly(anthonySocialAccounts);

        assertCachedUsersAre(anthony);
        assertCachedUserSocialAccountsAre(Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList()));
    }

    @Test
    void should_index_pull_request() {
        final var pullRequest = indexer.indexPullRequest("onlydustxyz", "marketplace-frontend", 1257L);

        assertThat(pullRequest.id()).isEqualTo(1524797398);
        assertThat(pullRequest.author().login()).isEqualTo("AnthonyBuisset");
        assertThat(pullRequest.reviews().get(0).id()).isEqualTo(pr1257Reviews[0].getId());
        assertThat(pullRequest.reviews().get(0).author().login()).isEqualTo("PierreOucif");
        assertThat(pullRequest.author().login()).isEqualTo("AnthonyBuisset");
        assertThat(pullRequest.requestedReviewers().get(0).login()).isEqualTo("ofux");
        assertThat(pullRequest.commits().size()).isEqualTo(1);
        assertThat(pullRequest.commits().get(0).sha()).isEqualTo("0addbe7d8cdbe1356fc8fb58e4b896616e7d7592");
        assertThat(pullRequest.commits().get(0).author().login()).isEqualTo("AnthonyBuisset");
        assertThat(pullRequest.checkRuns().size()).isEqualTo(17);
        assertThat(pullRequest.checkRuns().get(0).id()).isEqualTo(17002823375L);

        assertCachedPullRequestsAre(pr1257);
        assertCachedCodeReviewsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Reviews).toList()));
        assertCachedCommitsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Commits).toList()));
        assertCachedCheckRunsAre(Map.of(Tuple.tuple(pr1257.getHead().getRepo().getId(), pr1257.getHead().getSha()), pr1257CheckRuns));
        assertCachedUsersAre(anthony, pierre, olivier, anthony);
        assertCachedUserSocialAccountsAre(
                Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList(),
                        pierre.getId(), Arrays.stream(pierreSocialAccounts).toList(),
                        olivier.getId(), Arrays.stream(olivierSocialAccounts).toList())
        );
    }

    @Test
    void should_index_issue() {
        final var issue = indexer.indexIssue("onlydustxyz", "marketplace-frontend", 78L);

        assertThat(issue.id()).isEqualTo(1301824165);
        assertThat(issue.assignees().size()).isEqualTo(1);
        assertThat(issue.assignees().get(0).login()).isEqualTo("AnthonyBuisset");

        assertCachedIssuesAre(issue78);
        assertCachedUsersAre(anthony);
        assertCachedUserSocialAccountsAre(Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList()));
    }

    @Test
    void should_throw_when_indexing_non_existing_items() {
        assertThatThrownBy(() -> {
            indexer.indexUser(0L);
        }).isInstanceOf(NotFound.class)
                .hasMessageContaining("User not found");

        assertCachedUsersAre();
    }

    private void assertCachedUsersAre(RawUser... rawUsers) {
        assertThat(rawStorageRepository.users()).containsExactly(rawUsers);
    }

    private void assertCachedUserSocialAccountsAre(Map<Long, List<RawSocialAccount>> expected) {
        assertThat(rawStorageRepository.userSocialAccounts()).isEqualTo(expected);
    }

    private void assertCachedPullRequestsAre(RawPullRequest... expected) {
        assertThat(rawStorageRepository.pullRequests()).containsExactly(expected);
    }

    private void assertCachedIssuesAre(RawIssue... expected) {
        assertThat(rawStorageRepository.issues()).containsExactly(expected);
    }

    private void assertCachedCodeReviewsAre(Map<Long, List<RawCodeReview>> expected) {
        assertThat(rawStorageRepository.codeReviews()).isEqualTo(expected);
    }

    private void assertCachedCommitsAre(Map<Long, List<RawCommit>> expected) {
        assertThat(rawStorageRepository.commits()).isEqualTo(expected);
    }

    private void assertCachedCheckRunsAre(Map<Tuple, RawCheckRuns> expected) {
        assertThat(rawStorageRepository.checkRuns()).isEqualTo(expected);
    }
}
