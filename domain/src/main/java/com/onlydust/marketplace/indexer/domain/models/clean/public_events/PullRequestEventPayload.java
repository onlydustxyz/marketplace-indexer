package com.onlydust.marketplace.indexer.domain.models.clean.public_events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPullRequestEventPayload;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Slf4j
public class PullRequestEventPayload extends PublicEvent.Payload {
    CleanPullRequest pullRequest;

    public static PullRequestEventPayload of(RawPullRequestEventPayload rawPullRequestEventPayload) {
        return PullRequestEventPayload.builder()
                .pullRequest(CleanPullRequest.of(rawPullRequestEventPayload.pullRequest()))
                .build();
    }
}
