package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "users", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SQLInsert(sql = "INSERT INTO indexer_raw.users (data, login, id) VALUES (?, ?, ?) ON CONFLICT DO NOTHING")
public class RawUserEntity {
    @Id
    Long id;

    String login;

    @Type(type = "jsonb")
    RawAccount data;

    public static RawUserEntity of(RawAccount user) {
        return RawUserEntity.builder().id(user.getId()).login(user.getLogin()).data(user).build();
    }
}
