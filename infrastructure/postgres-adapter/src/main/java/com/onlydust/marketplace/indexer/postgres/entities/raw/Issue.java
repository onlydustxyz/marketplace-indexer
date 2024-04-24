package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "issues", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SQLInsert(sql = "INSERT INTO indexer_raw.issues (data, number, repo_id, id) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class Issue {
    @Id
    Long id;

    Long repoId;

    Long number;

    @Type(type = "jsonb")
    RawIssue data;

    public static Issue of(Long repoId, RawIssue issue) {
        return Issue.builder().id(issue.getId()).repoId(repoId).number(issue.getNumber()).data(issue).build();
    }
}
