package com.onlydust.marketplace.indexer.domain.services.observers;

import com.onlydust.marketplace.indexer.domain.models.raw.RawLabel;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawIssueCommentEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawPullRequestEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import lombok.AllArgsConstructor;
import onlydust.com.marketplace.kernel.model.event.*;
import onlydust.com.marketplace.kernel.port.output.OutboxPort;

import java.time.ZoneOffset;

import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
public class GithubOutboxObserver implements GithubObserver {
    private final OutboxPort outboxPort;

    @Override
    public void on(RawIssueEvent event) {
        switch (event.getAction()) {
            case "assigned" -> outboxPort.push(OnGithubIssueAssigned.builder()
                    .id(event.getIssue().getId())
                    .repoId(event.getRepository().getId())
                    .assigneeId(event.getAssignee().getId())
                    .assignedById(event.getSender().getId())
                    .labels(event.getIssue().getLabels().stream().map(RawLabel::getName).collect(toSet()))
                    .createdAt(event.getIssue().getCreatedAt().toInstant().atZone(ZoneOffset.UTC))
                    .assignedAt(event.getIssue().getUpdatedAt().toInstant().atZone(ZoneOffset.UTC))
                    .build());

            case "transferred" -> outboxPort.push(OnGithubIssueTransferred.builder()
                    .id(event.getIssue().getId())
                    .build());

            case "deleted" -> outboxPort.push(OnGithubIssueDeleted.builder()
                    .id(event.getIssue().getId())
                    .build());
        }
    }

    @Override
    public void on(RawPullRequestEvent event) {
        switch (event.getAction()) {
            case "opened" -> outboxPort.push(OnPullRequestCreated.builder()
                    .id(event.getPullRequest().getId())
                    .repoId(event.getRepository().getId())
                    .authorId(event.getPullRequest().getAuthor().getId())
                    .createdAt(event.getPullRequest().getCreatedAt().toInstant().atZone(ZoneOffset.UTC))
                    .build());

            case "closed" -> {
                if (event.getPullRequest().getMerged())
                    outboxPort.push(OnPullRequestMerged.builder()
                            .id(event.getPullRequest().getId())
                            .repoId(event.getRepository().getId())
                            .authorId(event.getPullRequest().getAuthor().getId())
                            .createdAt(event.getPullRequest().getCreatedAt().toInstant().atZone(ZoneOffset.UTC))
                            .mergedAt(event.getPullRequest().getMergedAt().toInstant().atZone(ZoneOffset.UTC))
                            .build());
            }
        }
    }

    @Override
    public void on(RawIssueCommentEvent event) {
        if (event.getIssue().getPullRequest() != null)
            return;

        if (!event.getComment().getAuthor().getType().equals("User"))
            return;

        switch (event.getAction()) {
            case "created" -> outboxPort.push(OnGithubCommentCreated.builder()
                    .id(event.getComment().getId())
                    .issueId(event.getIssue().getId())
                    .repoId(event.getRepository().getId())
                    .authorId(event.getComment().getAuthor().getId())
                    .createdAt(event.getComment().getCreatedAt().toInstant().atZone(ZoneOffset.UTC))
                    .body(event.getComment().getBody())
                    .build());

            case "edited" -> outboxPort.push(OnGithubCommentEdited.builder()
                    .id(event.getComment().getId())
                    .issueId(event.getIssue().getId())
                    .repoId(event.getRepository().getId())
                    .authorId(event.getComment().getAuthor().getId())
                    .updatedAt(event.getComment().getUpdatedAt().toInstant().atZone(ZoneOffset.UTC))
                    .body(event.getComment().getBody())
                    .build());

            case "deleted" -> outboxPort.push(OnGithubCommentDeleted.builder()
                    .id(event.getComment().getId())
                    .build());
        }
    }
}
