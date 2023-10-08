package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "issues", schema = "indexer_raw")
public class Issue {
    @Id
    Long id;
    Long repoId;
    Long number;
    @Type(type = "jsonb")
    RawIssue data;
    @CreationTimestamp
    ZonedDateTime createdAt;
    @UpdateTimestamp
    ZonedDateTime updatedAt;

    public static Issue of(Long repoId, RawIssue issue) {
        return Issue.builder().id(issue.getId()).repoId(repoId).number(issue.getNumber()).data(issue).build();
    }
}
