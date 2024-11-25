package com.onlydust.marketplace.indexer.domain.models.clean.github_app_events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawRepositoryEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class RepositoryEvent extends GithubAppEvent {
    String action;
    CleanRepo repository;

    public static RepositoryEvent of(RawRepositoryEvent event) {
        return new RepositoryEvent(event.getAction(), CleanRepo.of(event.getRepository()));
    }
}
