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
    final RawRepo marketplaceFrontend = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend.json", RawRepo.class);
    final RawLanguages marketplaceFrontendLanguages = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend/languages.json", RawLanguages.class);
    final RawStorageRepositoryStub rawStorageReader = new RawStorageRepositoryStub();
    final RawStorageRepositoryStub rawStorageRepository = new RawStorageRepositoryStub();
    final IndexingService indexer = new IndexingService(new CacheWriteRawStorageReaderDecorator(rawStorageReader, rawStorageRepository));

    @BeforeEach
    void setup() throws IOException {
        rawStorageReader.feedWith(marketplaceFrontend);
        rawStorageReader.feedWith(marketplaceFrontend.getId(), marketplaceFrontendLanguages);
        rawStorageReader.feedWith(anthony, pierre, olivier);
        rawStorageReader.feedWith(anthony.getId(), anthonySocialAccounts);
        rawStorageReader.feedWith(pierre.getId(), pierreSocialAccounts);
        rawStorageReader.feedWith(olivier.getId(), olivierSocialAccounts);
        rawStorageReader.feedWith(pr1257);
        rawStorageReader.feedWith(marketplaceFrontend.getId(), pr1257);
        rawStorageReader.feedWith(marketplaceFrontend.getId(), issue78);
        rawStorageReader.feedWith("onlydustxyz", "marketplace-frontend", 1257L, 78L);
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
        assertThat(pullRequest.closingIssues().size()).isEqualTo(1);
        assertThat(pullRequest.closingIssues().get(0).id()).isEqualTo(issue78.getId());

        assertCachedPullRequestsAre(pr1257);
        assertCachedIssuesAre(issue78);
        assertCachedClosingIssuesAre(Map.of(Tuple.tuple(marketplaceFrontend.getOwner().getLogin(), marketplaceFrontend.getName(), 1257L), List.of(78L)));
        assertCachedCodeReviewsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Reviews).toList()));
        assertCachedCommitsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Commits).toList()));
        assertCachedCheckRunsAre(Map.of(Tuple.tuple(pr1257.getHead().getRepo().getId(), pr1257.getHead().getSha()), pr1257CheckRuns));
        assertCachedUsersAre(
                anthony, // as PR author
                pierre,  // as code reviewer
                olivier, // as requested reviewer
                anthony, // as committer
                anthony  // as issue assignee
        );
        assertCachedUserSocialAccountsAre(
                Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList(),
                        pierre.getId(), Arrays.stream(pierreSocialAccounts).toList(),
                        olivier.getId(), Arrays.stream(olivierSocialAccounts).toList())
        );
    }

    @Test
    void should_index_issue() {
        final var issue = indexer.indexIssue("onlydustxyz", "marketplace-frontend", 78L);

        assertThat(issue.id()).isEqualTo(issue78.getId());
        assertThat(issue.assignees().size()).isEqualTo(1);
        assertThat(issue.assignees().get(0).login()).isEqualTo("AnthonyBuisset");

        assertCachedIssuesAre(issue78);
        assertCachedUsersAre(anthony);
        assertCachedUserSocialAccountsAre(Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList()));
    }

    @Test
    void should_index_repo() {
        final var repo = indexer.indexRepo(marketplaceFrontend.getId());

        assertThat(repo.id()).isEqualTo(marketplaceFrontend.getId());

        assertThat(repo.pullRequests().size()).isEqualTo(1);
        assertThat(repo.pullRequests().get(0).id()).isEqualTo(pr1257.getId());
        assertThat(repo.issues().size()).isEqualTo(1);
        assertThat(repo.issues().get(0).id()).isEqualTo(issue78.getId());
        assertThat(repo.languages().get("TypeScript")).isEqualTo(2761826);

        assertCachedReposAre(marketplaceFrontend);
        assertCachedRepoLanguagesAre(Map.of(marketplaceFrontend.getId(), marketplaceFrontendLanguages));
        assertCachedRepoPullRequestsAre(Map.of(marketplaceFrontend.getId(), List.of(pr1257)));
        assertCachedRepoIssuesAre(Map.of(marketplaceFrontend.getId(), List.of(issue78)));
        assertCachedPullRequestsAre(pr1257);
        assertCachedIssuesAre(
                issue78, // as pr 1257 closing issue
                issue78 // as repo issue
        );
        assertCachedClosingIssuesAre(Map.of(Tuple.tuple(marketplaceFrontend.getOwner().getLogin(), marketplaceFrontend.getName(), 1257L), List.of(78L)));
        assertCachedCodeReviewsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Reviews).toList()));
        assertCachedCommitsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Commits).toList()));
        assertCachedCheckRunsAre(Map.of(Tuple.tuple(pr1257.getHead().getRepo().getId(), pr1257.getHead().getSha()), pr1257CheckRuns));
        assertCachedUsersAre(
                anthony, // as PR author
                pierre,  // as code reviewer
                olivier, // as requested reviewer
                anthony, // as committer
                anthony, // as issue assignee
                anthony  // as issue assignee, again...
        );
        assertCachedUserSocialAccountsAre(
                Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList(),
                        pierre.getId(), Arrays.stream(pierreSocialAccounts).toList(),
                        olivier.getId(), Arrays.stream(olivierSocialAccounts).toList())
        );
    }

    @Test
    void should_throw_when_indexing_non_existing_items() {
        assertThatThrownBy(() -> {
            indexer.indexUser(0L);
        }).isInstanceOf(NotFound.class)
                .hasMessageContaining("User not found");

        assertCachedUsersAre();
    }

    private void assertCachedReposAre(RawRepo... repos) {
        assertThat(rawStorageRepository.repos()).containsExactly(repos);
    }

    private void assertCachedUsersAre(RawUser... users) {
        assertThat(rawStorageRepository.users()).containsExactly(users);
    }

    private void assertCachedUserSocialAccountsAre(Map<Long, List<RawSocialAccount>> expected) {
        assertThat(rawStorageRepository.userSocialAccounts()).isEqualTo(expected);
    }

    private void assertCachedRepoPullRequestsAre(Map<Long, List<RawPullRequest>> expected) {
        assertThat(rawStorageRepository.repoPullRequests()).isEqualTo(expected);
    }

    private void assertCachedRepoIssuesAre(Map<Long, List<RawIssue>> expected) {
        assertThat(rawStorageRepository.repoIssues()).isEqualTo(expected);
    }

    private void assertCachedRepoLanguagesAre(Map<Long, RawLanguages> expected) {
        assertThat(rawStorageRepository.repoLanguages()).isEqualTo(expected);
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

    private void assertCachedClosingIssuesAre(Map<Tuple, List<Long>> expected) {
        assertThat(rawStorageRepository.closingIssues()).isEqualTo(expected);
    }
}
