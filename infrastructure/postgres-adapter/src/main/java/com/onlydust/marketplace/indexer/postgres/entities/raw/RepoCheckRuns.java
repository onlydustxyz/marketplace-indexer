package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCheckRuns;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@IdClass(RepoCheckRuns.Id.class)
@Table(name = "repo_check_runs", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class RepoCheckRuns {
    @javax.persistence.Id
    Long repoId;

    @javax.persistence.Id
    String sha;

    @Type(type = "jsonb")
    RawCheckRuns data;

    public static RepoCheckRuns of(Long repoId, String sha, RawCheckRuns checkRuns) {
        return RepoCheckRuns.builder().repoId(repoId).sha(sha).data(checkRuns).build();
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Id implements Serializable {
        Long repoId;
        String sha;
    }
}
