package com.onlydust.marketplace.indexer.domain.models.clean.github_app_events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawInstallationEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public abstract class InstallationEvent extends GithubAppEvent {
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

            default -> null;
        };
    }

    public enum Action {
        CREATED, DELETED, ADDED, REMOVED, SUSPEND, UNSUSPEND, NEW_PERMISSIONS_ACCEPTED, REMOVE
    }

    public enum Permission {
        read, write
    }
}
