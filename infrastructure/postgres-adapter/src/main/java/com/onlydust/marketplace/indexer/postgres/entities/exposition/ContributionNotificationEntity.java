package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Set;

@Entity
@Value
@NoArgsConstructor(force = true)
public class ContributionNotificationEntity {
    @Id
    Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    Set<Long> repoIds;
    Instant lastUpdatedAt;
}
