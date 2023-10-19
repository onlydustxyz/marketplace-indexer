package com.onlydust.marketplace.indexer.postgres.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@EqualsAndHashCode
@Builder
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "user_indexing_job_triggers", schema = "indexer")
public class UserIndexingJobTriggerEntity {
    @Id
    Long userId;
}
