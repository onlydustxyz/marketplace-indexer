package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationRemovedEvent extends InstallationEvent {
    Long accountId;
    List<Long> repoIds;

    private InstallationRemovedEvent(Long installationId, Map<String, Permission> permissions, Long accountId, List<Long> repoIds) {
        super(installationId, Action.REMOVED, permissions);
        this.accountId = accountId;
        this.repoIds = repoIds;
    }

    public static InstallationRemovedEvent of(RawInstallationEvent rawEvent) {
        return new InstallationRemovedEvent(
                rawEvent.getInstallation().getId(),
                rawEvent.getInstallation().getPermissions(),
                rawEvent.getInstallation().getAccount().getId(),
                rawEvent.getRepositoriesRemoved().stream().map(RawRepo::getId).toList()
        );
    }
}
