package com.onlydust.marketplace.indexer.domain.models.clean;

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

    public enum Action {
        CREATED, DELETED, ADDED, REMOVED, SUSPEND, UNSUSPEND;

        public static Action of(String rawAction) {
            return switch (rawAction.toUpperCase()) {
                case "CREATED" -> CREATED;
                case "DELETED" -> DELETED;
                case "ADDED" -> ADDED;
                case "REMOVED" -> REMOVED;
                case "SUSPEND" -> SUSPEND;
                case "UNSUSPEND" -> UNSUSPEND;
                default -> null;
            };
        }
    }

    public enum Permission {
        read, write
    }
}
