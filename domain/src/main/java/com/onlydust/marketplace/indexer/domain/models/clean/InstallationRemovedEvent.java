package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationRemovedEvent extends InstallationEvent {
    Long accountId;
    List<Long> repoIds;

    private InstallationRemovedEvent(Long installationId, Long accountId, List<Long> repoIds) {
        super(installationId, Action.REMOVED);
        this.accountId = accountId;
        this.repoIds = repoIds;
    }

    public static InstallationRemovedEvent of(RawInstallationEvent rawEvent) {
        return new InstallationRemovedEvent(
                rawEvent.getInstallation().getId(),
                rawEvent.getInstallation().getAccount().getId(),
                rawEvent.getRepositoriesRemoved().stream().map(RawRepo::getId).toList()
        );
    }
}
