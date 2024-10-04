package com.onlydust.marketplace.indexer.domain.models.clean.github_app_events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawStarEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class StarEvent extends GithubAppEvent {
    CleanRepo repository;

    public static StarEvent of(RawStarEvent event) {
        return new StarEvent(
                CleanRepo.of(event.getRepository(), CleanAccount.of(event.getRepository().getOwner()))
        );
    }
}
