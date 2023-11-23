package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Set;

@Entity
@Value
@NoArgsConstructor(force = true)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ContributionNotificationEntity {
    @Id
    Long id;

    @Type(type = "jsonb")
    Set<Long> repoIds;
    Instant lastUpdatedAt;
}
