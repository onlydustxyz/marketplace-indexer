package com.onlydust.marketplace.indexer.domain.models.clean.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepositoryEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class RepositoryEvent extends Event {
    Action action;
    CleanRepo repository;

    public static RepositoryEvent of(RawRepositoryEvent event) {
        return new RepositoryEvent(Action.of(event.getAction()), CleanRepo.of(event.getRepository()));
    }

    public enum Action {
        PUBLICIZED, PRIVATIZED, DELETED, CREATED;

        public static Action of(String rawAction) {
            return switch (rawAction.toUpperCase()) {
                case "CREATED" -> CREATED;
                case "PUBLICIZED" -> PUBLICIZED;
                case "PRIVATIZED" -> PRIVATIZED;
                case "DELETED" -> DELETED;
                default -> null;
            };
        }
    }
}
