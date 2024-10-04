package com.onlydust.marketplace.indexer.domain.models.clean.github_app_events;

import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawInstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationUnsuspendedEvent extends InstallationEvent {
    private InstallationUnsuspendedEvent(Long installationId, Map<String, Permission> permissions) {
        super(installationId, Action.UNSUSPEND, permissions);
    }

    public static InstallationUnsuspendedEvent of(RawInstallationEvent rawEvent) {
        return new InstallationUnsuspendedEvent(rawEvent.getInstallation().getId(), rawEvent.getInstallation().getPermissions());
    }
}
