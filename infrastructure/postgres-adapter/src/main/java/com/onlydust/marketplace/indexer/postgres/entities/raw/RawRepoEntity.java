package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;


@Getter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "repos", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.repos (data, deleted, name, owner, id) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class RawRepoEntity {
    @Id
    Long id;

    String owner;

    String name;

    @JdbcTypeCode(SqlTypes.JSON)
    RawRepo data;

    @Builder.Default
    Boolean deleted = Boolean.FALSE;

    public static RawRepoEntity of(RawRepo repo) {
        return RawRepoEntity.builder().id(repo.getId()).owner(repo.getOwner().getLogin()).name(repo.getName()).data(repo).build();
    }
}
