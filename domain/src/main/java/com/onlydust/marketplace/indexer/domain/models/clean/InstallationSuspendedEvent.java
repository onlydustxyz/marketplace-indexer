package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationSuspendedEvent extends InstallationEvent {
    ZonedDateTime suspendedAt;

    private InstallationSuspendedEvent(Long installationId, ZonedDateTime suspendedAt) {
        super(installationId, Action.SUSPEND);
        this.suspendedAt = suspendedAt;
    }

    public static InstallationSuspendedEvent of(RawInstallationEvent rawEvent) {
        return new InstallationSuspendedEvent(rawEvent.getInstallation().getId(), rawEvent.getInstallation().getSuspendedAt());
    }
}
