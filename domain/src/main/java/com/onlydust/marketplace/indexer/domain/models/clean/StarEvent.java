package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawStarEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class StarEvent extends Event {
    CleanRepo repository;

    public static StarEvent of(RawStarEvent event) {
        return new StarEvent(
                CleanRepo.of(event.getRepository(), CleanAccount.of(event.getRepository().getOwner()))
        );
    }
}
