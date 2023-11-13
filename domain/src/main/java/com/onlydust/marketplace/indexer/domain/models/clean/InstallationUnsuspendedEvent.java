package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationUnsuspendedEvent extends InstallationEvent {
    private InstallationUnsuspendedEvent(Long installationId) {
        super(installationId, Action.UNSUSPEND);
    }

    public static InstallationUnsuspendedEvent of(RawInstallationEvent rawEvent) {
        return new InstallationUnsuspendedEvent(rawEvent.getInstallation().getId());
    }
}
