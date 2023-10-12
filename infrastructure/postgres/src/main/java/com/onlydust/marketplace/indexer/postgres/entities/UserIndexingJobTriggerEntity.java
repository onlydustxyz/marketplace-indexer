package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.UserIndexingJobTrigger;
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

    public static UserIndexingJobTriggerEntity of(UserIndexingJobTrigger trigger) {
        return UserIndexingJobTriggerEntity.builder()
                .userId(trigger.userId())
                .build();
    }
}
