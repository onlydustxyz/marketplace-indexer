package com.onlydust.marketplace.indexer.domain.ports.out;

import java.util.Set;

public interface ApiClient {
    void onNewContributions(Set<Long> repoIds);
}
