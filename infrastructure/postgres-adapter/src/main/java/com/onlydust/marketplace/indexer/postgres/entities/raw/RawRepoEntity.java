package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;


@Getter
@Entity
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Setter
@Accessors(chain = true, fluent = true)
@Table(name = "repos", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.repos (data, deleted, name, owner, id) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class RawRepoEntity {
    @Id
    final Long id;

    final String owner;

    final String name;

    @JdbcTypeCode(SqlTypes.JSON)
    RawRepo data;

    @Builder.Default
    Boolean deleted = Boolean.FALSE;

    public static RawRepoEntity of(RawRepo repo) {
        return RawRepoEntity.builder()
                .id(repo.getId())
                .owner(repo.getOwner().getLogin())
                .name(repo.getName()).
                data(repo)
                .build();
    }
}
