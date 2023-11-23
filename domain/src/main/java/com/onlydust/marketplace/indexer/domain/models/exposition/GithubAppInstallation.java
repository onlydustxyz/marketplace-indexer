package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
public class GithubAppInstallation {
    Long id;
    GithubAccount account;
    List<GithubRepo> repos;
    ZonedDateTime suspendedAt;

    public static GithubAppInstallation of(InstallationEvent event, GithubAccount owner, List<GithubRepo> repos) {
        return GithubAppInstallation.builder()
                .id(event.getInstallationId())
                .account(owner)
                .repos(repos)
                .build();
    }
}
