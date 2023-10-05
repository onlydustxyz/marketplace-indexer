package com.onlydust.marketplace.indexer.domain.models.clean;

import java.util.List;

public record PullRequest(Long id, User author, List<CodeReview> reviews, List<User> requestedReviewers,
                          List<Commit> commits, List<CheckRun> checkRuns, List<Issue> closingIssues) {
}
