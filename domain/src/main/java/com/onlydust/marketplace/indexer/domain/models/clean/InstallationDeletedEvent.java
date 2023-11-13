package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationDeletedEvent extends InstallationEvent {
    private InstallationDeletedEvent(Long installationId) {
        super(installationId, Action.DELETED);
    }

    public static InstallationDeletedEvent of(RawInstallationEvent rawEvent) {
        return new InstallationDeletedEvent(rawEvent.getInstallation().getId());
    }
}
