package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawGithubAppEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_events", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.user_events (data, user_id, id) VALUES (?, ?, ?) ON CONFLICT DO NOTHING")
public class RawUserEventEntity {
    @Id
    @NonNull
    Long id;

    @NonNull
    Long userId;

    @NonNull
    ZonedDateTime timestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @NonNull
    RawGithubAppEvent data;
}
