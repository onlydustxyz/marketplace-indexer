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
        CREATED, DELETED, ADDED, REMOVED;

        public static Action of(String rawAction) {
            return valueOf(rawAction.toUpperCase());
        }
    }
}
