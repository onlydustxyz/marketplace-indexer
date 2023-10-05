package com.onlydust.marketplace.indexer.domain.models.clean;

import java.util.List;

public record Repo(Long id, List<PullRequest> pullRequests, List<Issue> issues) {
}
