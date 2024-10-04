package com.onlydust.marketplace.indexer.domain.models.raw.github_app_events;


import com.onlydust.marketplace.indexer.domain.models.raw.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawIssueCommentEvent extends JsonDocument {
    String action;
    RawIssue issue;
    RawComment comment;
    RawRepo repository;
    RawInstallation installation;
}
