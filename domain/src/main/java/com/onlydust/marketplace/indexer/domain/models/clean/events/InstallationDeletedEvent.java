package com.onlydust.marketplace.indexer.domain.models.clean.events;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationDeletedEvent extends InstallationEvent {
    private InstallationDeletedEvent(Long installationId, Map<String, Permission> permissions) {
        super(installationId, Action.DELETED, permissions);
    }

    public static InstallationDeletedEvent of(RawInstallationEvent rawEvent) {
        return new InstallationDeletedEvent(rawEvent.getInstallation().getId(), rawEvent.getInstallation().getPermissions());
    }
}
