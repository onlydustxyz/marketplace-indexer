package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationAddedEvent extends InstallationEvent {
    List<CleanRepo> reposAdded;

    private InstallationAddedEvent(Long installationId, List<CleanRepo> reposAdded) {
        super(installationId, Action.ADDED);
        this.reposAdded = reposAdded;
    }

    public static InstallationAddedEvent of(RawInstallationEvent rawEvent, List<CleanRepo> reposAdded) {
        return new InstallationAddedEvent(rawEvent.getInstallation().getId(), reposAdded);
    }
}
