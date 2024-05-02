package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.onlydust.marketplace.indexer.domain.models.clean.*;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawCodeReview;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


class GithubPullRequestTest {
    final CleanAccount onlyDust = CleanAccount.of(RawStorageWriterStub.load("/github/users/onlyDust.json", RawAccount.class));
    final CleanRepo marketplaceFrontend = CleanRepo.of(RawStorageWriterStub.load("/github/repos/marketplace-frontend.json", RawRepo.class), onlyDust);
    final RawPullRequest pr1257 = RawStorageWriterStub.load("/github/repos/marketplace-frontend/pulls/1257.json", RawPullRequest.class);
    final CleanAccount anthony = CleanAccount.of(RawStorageWriterStub.load("/github/users/anthony.json", RawAccount.class));
    final CleanAccount pierre = CleanAccount.of(RawStorageWriterStub.load("/github/users/pierre.json", RawAccount.class));

    private CleanCodeReview codeReview(String state) {
        final RawCodeReview codeReview;
        try {
            codeReview = JsonMapper.builder().findAndAddModules().build().readValue("""
                    {
                      "id": 1637731192,
                      "user": {
                        "login": "PierreOucif",
                        "id": 16590657,
                        "avatar_url": "https://avatars.githubusercontent.com/u/16590657?u=1a94dc2d2be3e5c199916efa42ae79e8893f817d&v=4",
                        "html_url": "https://github.com/PierreOucif",
                        "type": "User"
                      },
                      "state": "%s",
                      "submitted_at": "2023-09-21T12:45:43Z"
                    }
                    """.formatted(state), RawCodeReview.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return CleanCodeReview.of(codeReview, CleanAccount.of(codeReview.getAuthor()));
    }

    private GithubPullRequest pullRequest(List<CleanAccount> requestedReviewers, List<String> states) {
        final var codeReviews = states.stream().map(this::codeReview).toList();
        final List<CleanCommit> commits = List.of();
        final List<CleanIssue> closingIssues = List.of();
        return GithubPullRequest.of(CleanPullRequest.of(pr1257, marketplaceFrontend, anthony, codeReviews, requestedReviewers, commits, closingIssues));
    }

    @Test
    void should_compute_pull_request_review_state() {
        assertThat(pullRequest(List.of(), List.of("CHANGES_REQUESTED", "COMMENTED", "APPROVED", "DISMISSED", "PENDING")).getReviewState())
                .isEqualTo(GithubPullRequest.ReviewState.CHANGES_REQUESTED);

        assertThat(pullRequest(List.of(), List.of("COMMENTED", "APPROVED", "DISMISSED", "PENDING")).getReviewState())
                .isEqualTo(GithubPullRequest.ReviewState.APPROVED);

        assertThat(pullRequest(List.of(), List.of("COMMENTED", "DISMISSED", "PENDING")).getReviewState())
                .isEqualTo(GithubPullRequest.ReviewState.UNDER_REVIEW);

        assertThat(pullRequest(List.of(pierre), List.of()).getReviewState())
                .isEqualTo(GithubPullRequest.ReviewState.UNDER_REVIEW);

        assertThat(pullRequest(List.of(), List.of()).getReviewState())
                .isEqualTo(GithubPullRequest.ReviewState.PENDING_REVIEWER);
    }

    @Test
    void should_extract_main_file_extensions() {

        final var commits = List.of(
                CleanCommit.builder().modifiedFiles(Map.of("path/to/File1.rs", 10, "File2.rs", 5)).build(),
                CleanCommit.builder().modifiedFiles(Map.of(".git/changes", 100, "etc/Makefile", 100)).build(),
                CleanCommit.builder().modifiedFiles(Map.of("path.bak/to/File3.js", 5, "File4.js", 5)).build(),
                CleanCommit.builder().modifiedFiles(Map.of("File5.sh", 1)).build()
        );

        assertThat(GithubPullRequest.extractMainFileExtensions(commits))
                .containsExactly("rs", "js");
    }
}
