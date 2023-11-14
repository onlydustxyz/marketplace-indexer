package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.CacheReadRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.CacheWriteRawStorageReaderDecorator;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.services.exposers.IssueContributionExposer;
import com.onlydust.marketplace.indexer.domain.services.exposers.PullRequestContributionExposer;
import com.onlydust.marketplace.indexer.domain.services.indexers.*;
import com.onlydust.marketplace.indexer.domain.stubs.ContributionStorageStub;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IndexingServiceTest {
    final RawAccount onlyDust = RawStorageWriterStub.load("/github/users/onlyDust.json", RawAccount.class);
    final RawAccount anthony = RawStorageWriterStub.load("/github/users/anthony.json", RawAccount.class);
    final RawAccount pierre = RawStorageWriterStub.load("/github/users/pierre.json", RawAccount.class);
    final RawAccount olivier = RawStorageWriterStub.load("/github/users/olivier.json", RawAccount.class);
    final RawSocialAccount[] anthonySocialAccounts = RawStorageWriterStub.load("/github/users/anthony_social_accounts.json", RawSocialAccount[].class);
    final RawSocialAccount[] pierreSocialAccounts = RawStorageWriterStub.load("/github/users/pierre_social_accounts.json", RawSocialAccount[].class);
    final RawSocialAccount[] olivierSocialAccounts = RawStorageWriterStub.load("/github/users/olivier_social_accounts.json", RawSocialAccount[].class);
    final RawPullRequest pr1257 = RawStorageWriterStub.load("/github/repos/marketplace-frontend/pulls/1257.json", RawPullRequest.class);
    final RawIssue issue78 = RawStorageWriterStub.load("/github/repos/marketplace-frontend/issues/78.json", RawIssue.class);
    final RawCodeReview[] pr1257Reviews = RawStorageWriterStub.load("/github/repos/marketplace-frontend/pulls/1257_reviews.json", RawCodeReview[].class);
    final RawCommit[] pr1257Commits = RawStorageWriterStub.load("/github/repos/marketplace-frontend/pulls/1257_commits.json", RawCommit[].class);
    final RawCheckRuns pr1257CheckRuns = RawStorageWriterStub.load("/github/repos/marketplace-frontend/pulls/1257_check_runs.json", RawCheckRuns.class);
    final RawPullRequestClosingIssues pr1257ClosingIssues = RawStorageWriterStub.load("/github/repos/marketplace-frontend/pulls/1257_closing_issues.json", RawPullRequestClosingIssues.class);
    final RawRepo marketplaceFrontend = RawStorageWriterStub.load("/github/repos/marketplace-frontend.json", RawRepo.class);
    final RawLanguages marketplaceFrontendLanguages = RawStorageWriterStub.load("/github/repos/marketplace-frontend/languages.json", RawLanguages.class);
    final RawRepo marketplaceBackend = RawStorageWriterStub.load("/github/repos/marketplace-backend.json", RawRepo.class);
    final RawLanguages marketplaceBackendLanguages = RawStorageWriterStub.load("/github/repos/marketplace-backend/languages.json", RawLanguages.class);
    final RawStorageWriterStub rawStorageReaderStub = new RawStorageWriterStub();
    final RawStorageWriterStub rawStorageRepository = new RawStorageWriterStub();
    final ContributionStorageStub contributionRepository = new ContributionStorageStub();
    final RawStorageReader rawStorageReader = CacheWriteRawStorageReaderDecorator.builder().fetcher(rawStorageReaderStub).cache(rawStorageRepository).build();
    final UserIndexingService userIndexingService = new UserIndexingService(rawStorageReader);
    final RepoIndexingService repoIndexingService = new RepoIndexingService(rawStorageReader, userIndexingService);
    final IssueIndexer issueIndexer = new IssueContributionExposer(
            new IssueIndexingService(rawStorageReader, userIndexingService, repoIndexingService),
            contributionRepository
    );
    final PullRequestIndexer pullRequestIndexer = new PullRequestContributionExposer(
            new PullRequestIndexingService(rawStorageReader, userIndexingService, repoIndexingService, issueIndexer),
            contributionRepository
    );
    final FullRepoIndexingService fullRepoIndexingService = new FullRepoIndexingService(rawStorageReader, issueIndexer, pullRequestIndexer, repoIndexingService);

    @BeforeEach
    void setup() {
        rawStorageReaderStub.feedWith(marketplaceFrontend);
        rawStorageReaderStub.feedWith(marketplaceFrontend.getId(), marketplaceFrontendLanguages);
        rawStorageReaderStub.feedWith(marketplaceBackend);
        rawStorageReaderStub.feedWith(marketplaceBackend.getId(), marketplaceBackendLanguages);
        rawStorageReaderStub.feedWith(anthony, pierre, olivier, onlyDust);
        rawStorageReaderStub.feedWith(anthony.getId(), anthonySocialAccounts);
        rawStorageReaderStub.feedWith(pierre.getId(), pierreSocialAccounts);
        rawStorageReaderStub.feedWith(olivier.getId(), olivierSocialAccounts);
        rawStorageReaderStub.feedWith(marketplaceFrontend.getId(), pr1257);
        rawStorageReaderStub.feedWith(marketplaceFrontend.getId(), issue78);
        rawStorageReaderStub.feedWith("onlydustxyz", "marketplace-frontend", 1257L, pr1257ClosingIssues);
        rawStorageReaderStub.feedWith(pr1257.getId(), pr1257Reviews);
        rawStorageReaderStub.feedWith(pr1257.getId(), pr1257Commits);
        rawStorageReaderStub.feedWith(pr1257.getHead().getRepo().getId(), pr1257.getHead().getSha(), pr1257CheckRuns);
    }

    @Test
    void should_index_user_from_its_id() {
        final var user = userIndexingService.indexUser(anthony.getId()).orElseThrow();

        assertThat(user.getId()).isEqualTo(anthony.getId());
        assertThat(user.getLogin()).isEqualTo(anthony.getLogin());
        assertThat(user.getSocialAccounts()).containsExactly(anthonySocialAccounts);

        assertCachedUsersAre(anthony);
        assertCachedUserSocialAccountsAre(Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList()));
    }

    @Test
    void should_index_pull_request() {
        final var pullRequest = pullRequestIndexer.indexPullRequest("onlydustxyz", "marketplace-frontend", 1257L).orElseThrow();
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
        assertCachedClosingIssuesAre(Map.of(Tuple.tuple(marketplaceFrontend.getOwner().getLogin(), marketplaceFrontend.getName(), pr1257.getNumber()), pr1257ClosingIssues));
        assertCachedCodeReviewsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Reviews).toList()));
        assertCachedCommitsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Commits).toList()));
        assertCachedCheckRunsAre(Map.of(Tuple.tuple(pr1257.getHead().getRepo().getId(), pr1257.getHead().getSha()), pr1257CheckRuns));
        assertCachedUsersAre(
                onlyDust, // as PR repo owner
                anthony, // as PR author
                pierre,  // as code reviewer
                olivier, // as requested reviewer
                anthony, // as committer
                onlyDust,// as issue repo owner
                anthony, // as issue author
                anthony // as issue assignee
        );
        assertCachedUserSocialAccountsAre(
                Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList(),
                        pierre.getId(), Arrays.stream(pierreSocialAccounts).toList(),
                        olivier.getId(), Arrays.stream(olivierSocialAccounts).toList(),
                        onlyDust.getId(), List.of())
        );

        assertThat(contributionRepository.contributions()).hasSize(5);
        assertThat(contributionRepository.contributions().stream().filter(c -> c.getType().equals(Contribution.Type.PULL_REQUEST))).hasSize(2);
        assertThat(contributionRepository.contributions().stream().filter(c -> c.getType().equals(Contribution.Type.CODE_REVIEW))).hasSize(2);
        assertThat(contributionRepository.contributions().stream().filter(c -> c.getType().equals(Contribution.Type.ISSUE))).hasSize(1);
    }

    @Test
    void should_index_issue() {
        final var issue = issueIndexer.indexIssue("onlydustxyz", "marketplace-frontend", 78L).orElseThrow();

        assertThat(issue.getId()).isEqualTo(issue78.getId());
        assertThat(issue.getAssignees().size()).isEqualTo(1);
        assertThat(issue.getAssignees().get(0).getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(issue.getAuthor().getLogin()).isEqualTo("AnthonyBuisset");

        assertCachedReposAre(marketplaceFrontend);
        assertCachedRepoIssuesAre(Map.of(marketplaceFrontend.getId(), List.of(issue78)));
        assertCachedUsersAre(onlyDust, anthony, anthony);
        assertCachedUserSocialAccountsAre(Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList(), onlyDust.getId(), List.of()));

        assertThat(contributionRepository.contributions()).hasSize(1);
        assertThat(contributionRepository.contributions().stream().filter(c -> c.getType().equals(Contribution.Type.ISSUE))).hasSize(1);
    }

    @Test
    void should_index_repo() {
        final var repo = fullRepoIndexingService.indexFullRepo(marketplaceFrontend.getId()).orElseThrow();

        assertThat(repo.getId()).isEqualTo(marketplaceFrontend.getId());

        assertThat(repo.getLanguages().get("TypeScript")).isEqualTo(2761826);

        assertCachedReposAre(marketplaceFrontend, marketplaceFrontend, marketplaceFrontend, marketplaceFrontend);
        assertCachedRepoLanguagesAre(Map.of(marketplaceFrontend.getId(), marketplaceFrontendLanguages));
        assertCachedRepoPullRequestsAre(Map.of(marketplaceFrontend.getId(), List.of(pr1257)));
        assertCachedRepoIssuesAre(Map.of(marketplaceFrontend.getId(), List.of(
                issue78, // as pr 1257 closing issue
                issue78  // as repo issue
        )));
        assertCachedClosingIssuesAre(Map.of(Tuple.tuple(marketplaceFrontend.getOwner().getLogin(), marketplaceFrontend.getName(), pr1257.getNumber()), pr1257ClosingIssues));
        assertCachedCodeReviewsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Reviews).toList()));
        assertCachedCommitsAre(Map.of(pr1257.getId(), Arrays.stream(pr1257Commits).toList()));
        assertCachedCheckRunsAre(Map.of(Tuple.tuple(pr1257.getHead().getRepo().getId(), pr1257.getHead().getSha()), pr1257CheckRuns));
        assertCachedUsersAre(
                onlyDust,// as repo owner
                onlyDust,// as PR repo owner
                anthony, // as PR author
                pierre,  // as code reviewer
                olivier, // as requested reviewer
                anthony, // as committer
                onlyDust,// as issue repo owner
                anthony, // as issue author
                anthony, // as issue author, again...
                onlyDust,// as issue repo owner
                anthony, // as issue assignee
                anthony // as issue assignee, again...
        );
        assertCachedUserSocialAccountsAre(
                Map.of(anthony.getId(), Arrays.stream(anthonySocialAccounts).toList(),
                        pierre.getId(), Arrays.stream(pierreSocialAccounts).toList(),
                        olivier.getId(), Arrays.stream(olivierSocialAccounts).toList(),
                        onlyDust.getId(), List.of())
        );
    }

    @Test
    void should_index_parent_repo() {
        final var repo = fullRepoIndexingService.indexFullRepo(marketplaceBackend.getId()).orElseThrow();

        assertThat(repo.getId()).isEqualTo(marketplaceBackend.getId());

        assertThat(repo.getLanguages().get("Rust")).isEqualTo(817684);

        assertCachedReposAre(marketplaceBackend, marketplaceFrontend);
        assertCachedRepoLanguagesAre(Map.of(marketplaceFrontend.getId(), marketplaceFrontendLanguages, marketplaceBackend.getId(), marketplaceBackendLanguages));
        assertCachedRepoPullRequestsAre(Map.of());
        assertCachedRepoIssuesAre(Map.of());
        assertCachedClosingIssuesAre(Map.of());
        assertCachedCodeReviewsAre(Map.of());
        assertCachedCommitsAre(Map.of());
        assertCachedCheckRunsAre(Map.of());
        assertCachedUsersAre(
                onlyDust, // as repo owner
                onlyDust  // as parent repo owner
        );
        assertCachedUserSocialAccountsAre(Map.of(onlyDust.getId(), List.of()));
    }

    @Test
    void should_not_reindex_up_to_date_pull_requests() {
        final var cachedReader = CacheReadRawStorageReaderDecorator.builder().cache(rawStorageReader).fetcher(rawStorageReader).build();
        final var indexer = new FullRepoIndexingService(cachedReader, issueIndexer, pullRequestIndexer, repoIndexingService);

        final var cachedIssues = rawStorageReaderStub.repoIssues();
        final var cachedPullRequests = rawStorageReaderStub.repoPullRequests();

        indexer.indexFullRepo(marketplaceFrontend.getId()).orElseThrow();

        assertCachedRepoIssuesAre(cachedIssues);
        assertCachedRepoPullRequestsAre(cachedPullRequests);
    }


    @Test
    void should_not_throw_when_indexing_non_existing_items() {
        assertThat(userIndexingService.indexUser(0L)).isEmpty();
        assertCachedUsersAre();
    }


    @Test
    void should_not_fail_when_pr_indexing_fail() {
        final var pullRequestIndexer = mock(PullRequestIndexer.class);
        final var indexer = new FullRepoIndexingService(rawStorageReader, issueIndexer, pullRequestIndexer, repoIndexingService);

        when(pullRequestIndexer.indexPullRequest(any(), any(), any())).thenThrow(new RuntimeException("Unable to index PR"));

        indexer.indexFullRepo(marketplaceFrontend.getId()).orElseThrow();
    }

    @Test
    void should_not_fail_when_issue_indexing_fail() {
        final var issueIndexer = mock(IssueIndexer.class);
        final var indexer = new FullRepoIndexingService(rawStorageReader, issueIndexer, pullRequestIndexer, repoIndexingService);

        when(issueIndexer.indexIssue(any(), any(), any())).thenThrow(new RuntimeException("Unable to index issue"));

        indexer.indexFullRepo(marketplaceFrontend.getId()).orElseThrow();
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

    private void assertCachedClosingIssuesAre(Map<Tuple, RawPullRequestClosingIssues> expected) {
        assertThat(rawStorageRepository.closingIssues()).isEqualTo(expected);
    }
}
