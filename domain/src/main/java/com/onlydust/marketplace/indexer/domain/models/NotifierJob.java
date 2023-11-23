package com.onlydust.marketplace.indexer.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class NotifierJob {
    Long id;
    Instant lastNotificationSentAt;
}
