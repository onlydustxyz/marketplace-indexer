package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationNewPermissionsAcceptedEvent extends InstallationEvent {

    private InstallationNewPermissionsAcceptedEvent(Long installationId, Map<String, Permission> permissions) {
        super(installationId, Action.NEW_PERMISSIONS_ACCEPTED, permissions);
    }

    public static InstallationNewPermissionsAcceptedEvent of(RawInstallationEvent rawEvent) {
        return new InstallationNewPermissionsAcceptedEvent(rawEvent.getInstallation().getId(),
                rawEvent.getInstallation().getPermissions());
    }
}
