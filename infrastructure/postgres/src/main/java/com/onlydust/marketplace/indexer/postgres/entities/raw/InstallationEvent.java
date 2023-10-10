package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "installation_events", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class InstallationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Type(type = "jsonb")
    RawInstallationEvent data;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    public static InstallationEvent of(RawInstallationEvent event) {
        return InstallationEvent.builder().data(event).build();
    }
}
