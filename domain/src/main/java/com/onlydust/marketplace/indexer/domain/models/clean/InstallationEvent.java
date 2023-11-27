package com.onlydust.marketplace.indexer.domain.models.clean;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public abstract class InstallationEvent extends Event {
    Long installationId;
    Action action;

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
}
