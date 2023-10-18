package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.in.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.CacheWriteRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.services.*;
import com.onlydust.marketplace.indexer.domain.stubs.ContributionRepositoryStub;
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
    final RawAccount anthony = RawStorageRepositoryStub.load("/github/users/anthony.json", RawAccount.class);
    final RawAccount pierre = RawStorageRepositoryStub.load("/github/users/pierre.json", RawAccount.class);
    final RawAccount olivier = RawStorageRepositoryStub.load("/github/users/olivier.json", RawAccount.class);
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
    final RawStorageRepositoryStub rawStorageReaderStub = new RawStorageRepositoryStub();
    final RawStorageRepositoryStub rawStorageRepository = new RawStorageRepositoryStub();
    final ContributionRepositoryStub contributionRepository = new ContributionRepositoryStub();
    final RawStorageReader rawStorageReader = CacheWriteRawStorageReaderDecorator.builder().fetcher(rawStorageReaderStub).cache(rawStorageRepository).build();
    final UserIndexingService userIndexingService = new UserIndexingService(rawStorageReader);
    final IssueIndexingService issueIndexingService = new IssueIndexingService(rawStorageReader, userIndexingService);
    final PullRequestIndexer pullRequestIndexingService = new PullRequestContributionExposer(
            new PullRequestIndexingService(rawStorageReader, userIndexingService, issueIndexingService),
            contributionRepository
    );
    final RepoIndexingService repoIndexingService = new RepoIndexingService(rawStorageReader, issueIndexingService, pullRequestIndexingService);

    @BeforeEach
    void setup() throws IOException {
        rawStorageReaderStub.feedWith(marketplaceFrontend);
        rawStorageReaderStub.feedWith(marketplaceFrontend.getId(), marketplaceFrontendLanguages);
        rawStorageReaderStub.feedWith(anthony, pierre, olivier);
        rawStorageReaderStub.feedWith(anthony.getId(), anthonySocialAccounts);
        rawStorageReaderStub.feedWith(pierre.getId(), pierreSocialAccounts);
        rawStorageReaderStub.feedWith(olivier.getId(), olivierSocialAccounts);
        rawStorageReaderStub.feedWith(marketplaceFrontend.getId(), pr1257);
        rawStorageReaderStub.feedWith(marketplaceFrontend.getId(), issue78);
        rawStorageReaderStub.feedClosingIssuesWith(pr1257.getId(), issue78);
        rawStorageReaderStub.feedWith(pr1257.getId(), pr1257Reviews);
        rawStorageReaderStub.feedWith(pr1257.getId(), pr1257Commits);
        rawStorageReaderStub.feedWith(pr1257.getHead().getRepo().getId(), pr1257.getHead().getSha(), pr1257CheckRuns);
    }

    @Test
    void should_index_user_from_its_id() {
        final var user = userIndexingService.indexUser(anthony.getId());

        assertThat(user.getId()).isEqualTo(anthony.getId());
        assertThat(user.getLogin()).isEqualTo(anthony.getLogin());
        assertThat(user.getSocialAccounts()).containsExactly(anthonySocialAccounts);

        assertCachedUsersAre(anthony);
        assertCachedUserSocialAccountsAre(Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList()));
    }

    @Test
    void should_index_pull_request() {
        final var pullRequest = pullRequestIndexingService.indexPullRequest("onlydustxyz", "marketplace-frontend", 1257L);

        assertThat(pullRequest.getId()).isEqualTo(1524797398);
        assertThat(pullRequest.getAuthor().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(pullRequest.getReviews().get(0).getId()).isEqualTo(pr1257Reviews[0].getId());
        assertThat(pullRequest.getReviews().get(0).getAuthor().getLogin()).isEqualTo("PierreOucif");
        assertThat(pullRequest.getAuthor().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(pullRequest.getRequestedReviewers().get(0).getLogin()).isEqualTo("ofux");
        assertThat(pullRequest.getCommits().size()).isEqualTo(1);
        assertThat(pullRequest.getCommits().get(0).getSha()).isEqualTo("0addbe7d8cdbe1356fc8fb58e4b896616e7d7592");
        assertThat(pullRequest.getCommits().get(0).getAuthor().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(pullRequest.getCheckRuns().size()).isEqualTo(17);
        assertThat(pullRequest.getCheckRuns().get(0).getId()).isEqualTo(17002823375L);
        assertThat(pullRequest.getClosingIssues().size()).isEqualTo(1);
        assertThat(pullRequest.getClosingIssues().get(0).getId()).isEqualTo(issue78.getId());

        assertCachedReposAre(marketplaceFrontend, marketplaceFrontend);
        assertCachedRepoPullRequestsAre(Map.of(marketplaceFrontend.getId(), List.of(pr1257)));
        assertCachedRepoIssuesAre(Map.of(marketplaceFrontend.getId(), List.of(issue78)));
        assertCachedClosingIssuesAre(Map.of(pr1257.getId(), List.of(issue78.getId())));
        assertCachedCodeReviewsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Reviews).toList()));
        assertCachedCommitsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Commits).toList()));
        assertCachedCheckRunsAre(Map.of(Tuple.tuple(pr1257.getHead().getRepo().getId(), pr1257.getHead().getSha()), pr1257CheckRuns));
        assertCachedUsersAre(
                anthony, // as PR author
                pierre,  // as code reviewer
                olivier, // as requested reviewer
                anthony, // as committer
                anthony,  // as issue author
                anthony  // as issue assignee
        );
        assertCachedUserSocialAccountsAre(
                Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList(),
                        pierre.getId(), Arrays.stream(pierreSocialAccounts).toList(),
                        olivier.getId(), Arrays.stream(olivierSocialAccounts).toList())
        );

        assertThat(contributionRepository.contributions()).hasSize(4);
        assertThat(contributionRepository.contributions().stream().filter(c -> c.getType().equals(Contribution.Type.PULL_REQUEST))).hasSize(2);
        assertThat(contributionRepository.contributions().stream().filter(c -> c.getType().equals(Contribution.Type.CODE_REVIEW))).hasSize(2);
    }

    @Test
    void should_index_issue() {
        final var issue = issueIndexingService.indexIssue("onlydustxyz", "marketplace-frontend", 78L);

        assertThat(issue.getId()).isEqualTo(issue78.getId());
        assertThat(issue.getAssignees().size()).isEqualTo(1);
        assertThat(issue.getAssignees().get(0).getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(issue.getAuthor().getLogin()).isEqualTo("AnthonyBuisset");

        assertCachedReposAre(marketplaceFrontend);
        assertCachedRepoIssuesAre(Map.of(marketplaceFrontend.getId(), List.of(issue78)));
        assertCachedUsersAre(anthony, anthony);
        assertCachedUserSocialAccountsAre(Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList()));
    }

    @Test
    void should_index_repo() {
        final var repo = repoIndexingService.indexRepo(marketplaceFrontend.getId());

        assertThat(repo.getId()).isEqualTo(marketplaceFrontend.getId());

        assertThat(repo.getPullRequests().size()).isEqualTo(1);
        assertThat(repo.getPullRequests().get(0).getId()).isEqualTo(pr1257.getId());
        assertThat(repo.getIssues().size()).isEqualTo(1);
        assertThat(repo.getIssues().get(0).getId()).isEqualTo(issue78.getId());
        assertThat(repo.getLanguages().get("TypeScript")).isEqualTo(2761826);

        assertCachedReposAre(marketplaceFrontend, marketplaceFrontend, marketplaceFrontend, marketplaceFrontend);
        assertCachedRepoLanguagesAre(Map.of(marketplaceFrontend.getId(), marketplaceFrontendLanguages));
        assertCachedRepoPullRequestsAre(Map.of(marketplaceFrontend.getId(), List.of(pr1257, pr1257)));
        assertCachedRepoIssuesAre(Map.of(marketplaceFrontend.getId(), List.of(
                issue78, // as pr 1257 closing issue
                issue78, issue78  // as repo issue
        )));
        assertCachedClosingIssuesAre(Map.of(pr1257.getId(), List.of(issue78.getId())));
        assertCachedCodeReviewsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Reviews).toList()));
        assertCachedCommitsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Commits).toList()));
        assertCachedCheckRunsAre(Map.of(Tuple.tuple(pr1257.getHead().getRepo().getId(), pr1257.getHead().getSha()), pr1257CheckRuns));
        assertCachedUsersAre(
                anthony, // as PR author
                pierre,  // as code reviewer
                olivier, // as requested reviewer
                anthony, // as committer
                anthony, // as issue author
                anthony, // as issue author, again...
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
            userIndexingService.indexUser(0L);
        }).isInstanceOf(OnlyDustException.class)
                .hasMessageContaining("User not found");

        assertCachedUsersAre();
    }

    private void assertCachedReposAre(RawRepo... repos) {
        assertThat(rawStorageRepository.repos()).containsExactly(repos);
    }

    private void assertCachedUsersAre(RawAccount... users) {
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

    private void assertCachedCodeReviewsAre(Map<Long, List<RawCodeReview>> expected) {
        assertThat(rawStorageRepository.codeReviews()).isEqualTo(expected);
    }

    private void assertCachedCommitsAre(Map<Long, List<RawCommit>> expected) {
        assertThat(rawStorageRepository.commits()).isEqualTo(expected);
    }

    private void assertCachedCheckRunsAre(Map<Tuple, RawCheckRuns> expected) {
        assertThat(rawStorageRepository.checkRuns()).isEqualTo(expected);
    }

    private void assertCachedClosingIssuesAre(Map<Long, List<Long>> expected) {
        assertThat(rawStorageRepository.closingIssues()).isEqualTo(expected);
    }
}
