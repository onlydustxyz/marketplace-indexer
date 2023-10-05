package com.onlydust.marketplace.indexer.domain.models.clean;

import java.util.List;
import java.util.Map;

public record Repo(Long id, List<PullRequest> pullRequests, List<Issue> issues, Map<String, Long> languages) {
}
