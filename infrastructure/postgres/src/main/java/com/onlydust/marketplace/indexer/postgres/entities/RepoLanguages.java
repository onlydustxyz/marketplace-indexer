package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.raw.RawLanguages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "repo_languages", schema = "indexer_raw")
public class RepoLanguages {
    @Id
    @Column(name = "repo_id")
    Long repoId;
    @OneToOne(mappedBy = "repo_id")
    Repo repo;
    @Column(name = "data")
    @Type(type = "jsonb")
    RawLanguages data;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    ZonedDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    ZonedDateTime updatedAt;

    public static RepoLanguages of(Long repoId, RawLanguages languages) {
        return RepoLanguages.builder().repoId(repoId).data(languages).build();
    }
}
