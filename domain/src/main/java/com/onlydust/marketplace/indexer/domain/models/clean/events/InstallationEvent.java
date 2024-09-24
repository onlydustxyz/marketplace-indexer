package com.onlydust.marketplace.indexer.domain.models.clean.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public abstract class InstallationEvent extends Event {
    Long installationId;
    Action action;
    Map<String, Permission> permissions;

    public static InstallationEvent of(RawInstallationEvent rawEvent) {
        return switch (Action.valueOf(rawEvent.getAction().toUpperCase())) {
            case ADDED -> InstallationAddedEvent.of(
                    rawEvent,
                    rawEvent.getRepositoriesAdded().stream()
                            .map(repo -> CleanRepo.of(repo, CleanAccount.of(rawEvent.getInstallation().getAccount())))
                            .toList());

            case CREATED -> InstallationCreatedEvent.of(
                    rawEvent,
                    CleanAccount.of(rawEvent.getInstallation().getAccount()),
                    rawEvent.getRepositories().stream()
                            .map(repo -> CleanRepo.of(repo, CleanAccount.of(rawEvent.getInstallation().getAccount())))
                            .toList());

            case REMOVED -> InstallationRemovedEvent.of(rawEvent);

            case DELETED -> InstallationDeletedEvent.of(rawEvent);

            case SUSPEND -> InstallationSuspendedEvent.of(rawEvent);

            case UNSUSPEND -> InstallationUnsuspendedEvent.of(rawEvent);

            case NEW_PERMISSIONS_ACCEPTED -> InstallationNewPermissionsAcceptedEvent.of(rawEvent);
        };
    }

    public enum Action {
        CREATED, DELETED, ADDED, REMOVED, SUSPEND, UNSUSPEND, NEW_PERMISSIONS_ACCEPTED
    }

    public enum Permission {
        read, write
    }
}
