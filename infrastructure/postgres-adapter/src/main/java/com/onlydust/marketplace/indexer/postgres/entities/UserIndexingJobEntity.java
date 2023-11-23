package com.onlydust.marketplace.indexer.postgres.entities;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@EqualsAndHashCode
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "user_indexing_jobs", schema = "indexer")
@TypeDef(name = "job_status", typeClass = PostgreSQLEnumType.class)
@DynamicUpdate
public class UserIndexingJobEntity {
    @Id
    Long userId;

    @Enumerated(EnumType.STRING)
    @Type(type = "job_status")
    JobStatus status;

    ZonedDateTime startedAt;
    ZonedDateTime finishedAt;

    public UserIndexingJobEntity(Long userId) {

        this.userId = userId;
        this.status = JobStatus.PENDING;
    }
}
