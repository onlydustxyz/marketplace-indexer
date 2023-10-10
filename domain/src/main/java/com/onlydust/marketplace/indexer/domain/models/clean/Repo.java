package com.onlydust.marketplace.indexer.domain.models.clean;

import java.util.Date;
import java.util.List;
import java.util.Map;

public record Repo(Long id,
                   String name,
                   String htmlUrl,
                   Date updatedAt,
                   String description,
                   Long starsCount,
                   Long forksCount,
                   List<PullRequest> pullRequests,
                   List<Issue> issues,
                   Map<String, Long> languages) {
}
