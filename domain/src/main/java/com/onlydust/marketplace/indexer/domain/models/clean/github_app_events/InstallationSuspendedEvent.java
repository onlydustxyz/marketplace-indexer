package com.onlydust.marketplace.indexer.domain.models.clean.github_app_events;

import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawInstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Date;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationSuspendedEvent extends InstallationEvent {
    Date suspendedAt;

    private InstallationSuspendedEvent(Long installationId, Map<String, Permission> permissions, Date suspendedAt) {
        super(installationId, Action.SUSPEND, permissions);
        this.suspendedAt = suspendedAt;
    }

    public static InstallationSuspendedEvent of(RawInstallationEvent rawEvent) {
        return new InstallationSuspendedEvent(rawEvent.getInstallation().getId(),
                rawEvent.getInstallation().getPermissions(),
                rawEvent.getInstallation().getSuspendedAt());
    }
}
