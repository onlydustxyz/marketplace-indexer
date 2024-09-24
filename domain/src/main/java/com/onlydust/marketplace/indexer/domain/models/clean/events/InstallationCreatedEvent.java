package com.onlydust.marketplace.indexer.domain.models.clean.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class InstallationCreatedEvent extends InstallationEvent {
    CleanAccount account;
    List<CleanRepo> repos;

    private InstallationCreatedEvent(Long installationId, Map<String, Permission> permissions, CleanAccount account, List<CleanRepo> repos) {
        super(installationId, Action.CREATED, permissions);
        this.account = account;
        this.repos = repos;
    }

    public static InstallationCreatedEvent of(RawInstallationEvent rawEvent, CleanAccount account, List<CleanRepo> repos) {
        return new InstallationCreatedEvent(rawEvent.getInstallation().getId(), rawEvent.getInstallation().getPermissions(), account, repos);
    }
}
