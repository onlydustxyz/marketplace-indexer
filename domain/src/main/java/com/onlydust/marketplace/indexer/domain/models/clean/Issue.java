package com.onlydust.marketplace.indexer.domain.models.clean;

import java.util.List;

public record Issue(Long id, List<User> assignees) {
}
