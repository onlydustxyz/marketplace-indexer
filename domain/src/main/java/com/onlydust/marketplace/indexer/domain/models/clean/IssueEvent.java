package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssueEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class IssueEvent extends Event {
    CleanIssue issue;

    public static IssueEvent of(RawIssueEvent event) {
        return new IssueEvent(
                CleanIssue.of(event.getIssue(), CleanRepo.of(event.getRepository()))
        );
    }
}
