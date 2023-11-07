package com.onlydust.marketplace.indexer.postgres.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@EqualsAndHashCode
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "user_indexing_job_triggers", schema = "indexer")
public class UserIndexingJobTriggerEntity {
    @Id
    Long userId;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    Instant techCreatedAt;

    @EqualsAndHashCode.Exclude
    @UpdateTimestamp
    @Column(nullable = false)
    Instant techUpdatedAt;

    public UserIndexingJobTriggerEntity(Long userId) {
        this.userId = userId;
    }
}
