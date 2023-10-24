package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
public class GithubAppInstallation {
    Long id;
    GithubAccount account;

    public static GithubAppInstallation of(InstallationEvent event, GithubAccount owner) {
        return GithubAppInstallation.builder()
                .id(event.getInstallationId())
                .account(owner)
                .build();
    }
}
