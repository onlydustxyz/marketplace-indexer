package com.onlydust.marketplace.indexer.domain.services.observers;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawLabel;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import lombok.AllArgsConstructor;
import onlydust.com.marketplace.kernel.model.event.OnGithubIssueAssigned;
import onlydust.com.marketplace.kernel.model.event.OnPullRequestCreated;
import onlydust.com.marketplace.kernel.model.event.OnPullRequestMerged;
import onlydust.com.marketplace.kernel.port.output.OutboxPort;

import java.time.ZoneOffset;

import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
public class GithubOutboxObserver implements GithubObserver {
    private final OutboxPort outboxPort;

    @Override
    public void on(RawIssueEvent event) {
        if (event.getAction().equals("assigned"))
            outboxPort.push(OnGithubIssueAssigned.builder()
                    .id(event.getIssue().getId())
                    .repoId(event.getRepository().getId())
                    .assigneeId(event.getAssignee().getId())
                    .labels(event.getIssue().getLabels().stream().map(RawLabel::getName).collect(toSet()))
                    .createdAt(event.getIssue().getCreatedAt().toInstant().atZone(ZoneOffset.UTC))
                    .assignedAt(event.getIssue().getUpdatedAt().toInstant().atZone(ZoneOffset.UTC))
                    .build());
    }

    @Override
    public void on(RawPullRequestEvent event) {
        if (event.getAction().equals("opened"))
            outboxPort.push(OnPullRequestCreated.builder()
                    .id(event.getPullRequest().getId())
                    .repoId(event.getRepository().getId())
                    .authorId(event.getPullRequest().getAuthor().getId())
                    .createdAt(event.getPullRequest().getCreatedAt().toInstant().atZone(ZoneOffset.UTC))
                    .build());

        else if (event.getAction().equals("closed") && event.getPullRequest().getMerged())
            outboxPort.push(OnPullRequestMerged.builder()
                    .id(event.getPullRequest().getId())
                    .repoId(event.getRepository().getId())
                    .authorId(event.getPullRequest().getAuthor().getId())
                    .createdAt(event.getPullRequest().getCreatedAt().toInstant().atZone(ZoneOffset.UTC))
                    .mergedAt(event.getPullRequest().getMergedAt().toInstant().atZone(ZoneOffset.UTC))
                    .build());
    }
}
