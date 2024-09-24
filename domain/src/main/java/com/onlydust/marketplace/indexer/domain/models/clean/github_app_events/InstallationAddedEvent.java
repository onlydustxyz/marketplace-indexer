package com.onlydust.marketplace.indexer.domain.models.clean.github_app_events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawInstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationAddedEvent extends InstallationEvent {
    List<CleanRepo> reposAdded;

    private InstallationAddedEvent(Long installationId, Map<String, Permission> permissions, List<CleanRepo> reposAdded) {
        super(installationId, Action.ADDED, permissions);
        this.reposAdded = reposAdded;
    }

    public static InstallationAddedEvent of(RawInstallationEvent rawEvent, List<CleanRepo> reposAdded) {
        return new InstallationAddedEvent(rawEvent.getInstallation().getId(), rawEvent.getInstallation().getPermissions(), reposAdded);
    }
}
