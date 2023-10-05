package com.onlydust.marketplace.indexer.domain.model.clean;

import java.util.List;

public record PullRequest(Integer id, User author, List<CodeReview> reviews) {
}
