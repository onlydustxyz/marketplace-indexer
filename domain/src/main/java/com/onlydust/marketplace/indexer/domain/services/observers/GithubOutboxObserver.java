package com.onlydust.marketplace.indexer.domain.services.observers;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawLabel;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import lombok.AllArgsConstructor;
import onlydust.com.marketplace.kernel.model.event.OnGithubIssueAssigned;
import onlydust.com.marketplace.kernel.port.output.OutboxPort;

import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
public class GithubOutboxObserver implements GithubObserver {
    private final OutboxPort outboxPort;

    @Override
    public void on(RawIssueEvent event) {
        if (event.getAction().equals("assigned"))
            outboxPort.push(OnGithubIssueAssigned.builder()
                    .id(event.getIssue().getId())
                    .assigneeId(event.getAssignee().getId())
                    .labels(event.getIssue().getLabels().stream().map(RawLabel::getName).collect(toSet()))
                    .assignedAt(event.getIssue().getUpdatedAt().toInstant().atZone(ZoneOffset.UTC))
                    .build());
    }
}
