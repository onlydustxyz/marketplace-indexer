package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
@Value
public class CleanIssue {
    Long id;
    List<CleanAccount> assignees;

    public static CleanIssue of(RawIssue issue, List<CleanAccount> assignees) {
        return CleanIssue.builder()
                .id(issue.getId())
                .assignees(assignees)
                .build();
    }
}
