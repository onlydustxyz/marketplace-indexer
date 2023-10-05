package com.onlydust.marketplace.indexer.domain.model.clean;

import java.util.List;

public record PullRequest(Long id, User author, List<CodeReview> reviews, List<User> requestedReviewers,
                          List<Commit> commits, List<CheckRun> checkRuns) {
}
