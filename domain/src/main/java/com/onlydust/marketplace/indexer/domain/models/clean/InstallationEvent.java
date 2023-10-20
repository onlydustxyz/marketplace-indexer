package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class InstallationEvent extends Event {
    Action action;
    Long installationId;
    CleanAccount account;
    List<CleanRepo> repos;

    public static InstallationEvent of(RawInstallationEvent rawEvent, CleanAccount account, List<CleanRepo> repos) {
        return InstallationEvent.builder()
                .action(Action.of(rawEvent.getAction()))
                .installationId(rawEvent.getInstallation().getId())
                .account(account)
                .repos(repos)
                .build();
    }

    public enum Action {
        CREATED, DELETED;

        public static Action of(String rawAction) {
            return valueOf(rawAction.toUpperCase());
        }
    }
}
