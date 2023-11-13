package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationCreatedEvent extends InstallationEvent {
    CleanAccount account;
    List<CleanRepo> repos;

    private InstallationCreatedEvent(Long installationId, CleanAccount account, List<CleanRepo> repos) {
        super(installationId, Action.CREATED);
        this.account = account;
        this.repos = repos;
    }

    public static InstallationCreatedEvent of(RawInstallationEvent rawEvent, CleanAccount account, List<CleanRepo> repos) {
        return new InstallationCreatedEvent(rawEvent.getInstallation().getId(), account, repos);
    }
}
