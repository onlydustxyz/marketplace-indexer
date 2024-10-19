package com.onlydust.marketplace.indexer.domain.ports.out;


import onlydust.com.marketplace.kernel.model.ContributionUUID;

public interface IndexingObserver {
    void onContributionsChanged(Long repoId, ContributionUUID contributionUUID);
}
