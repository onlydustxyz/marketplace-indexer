package com.onlydust.marketplace.indexer.domain.models.clean;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class InstallationEvent extends Event {
    Action action;
    Long installationId;
    User account;
    List<Repo> repos;

    public enum Action {
        CREATED,
        DELETED
    }
}
