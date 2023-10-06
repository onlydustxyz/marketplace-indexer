package com.onlydust.marketplace.indexer.domain.models.raw;


import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public record RawPullRequestClosingIssues(Long pullRequestId, List<Pair<Long, Long>> issueIdNumbers) {
}
