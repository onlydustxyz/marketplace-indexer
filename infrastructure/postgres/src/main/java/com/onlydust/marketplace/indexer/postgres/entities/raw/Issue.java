package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "issues", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Issue {
    @Id
    Long id;

    Long repoId;

    Long number;

    @Type(type = "jsonb")
    RawIssue data;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @EqualsAndHashCode.Exclude
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    public static Issue of(Long repoId, RawIssue issue) {
        return Issue.builder().id(issue.getId()).repoId(repoId).number(issue.getNumber()).data(issue).build();
    }
}
