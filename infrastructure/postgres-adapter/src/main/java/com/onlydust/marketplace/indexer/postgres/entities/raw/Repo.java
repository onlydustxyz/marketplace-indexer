package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
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
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "repos", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SQLInsert(sql = "INSERT INTO indexer_raw.repos (data, deleted, name, owner, id) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class Repo {
    @Id
    Long id;

    String owner;

    String name;

    @Type(type = "jsonb")
    RawRepo data;

    @Builder.Default
    Boolean deleted = Boolean.FALSE;

    public static Repo of(RawRepo repo) {
        return Repo.builder().id(repo.getId()).owner(repo.getOwner().getLogin()).name(repo.getName()).data(repo).build();
    }
}
