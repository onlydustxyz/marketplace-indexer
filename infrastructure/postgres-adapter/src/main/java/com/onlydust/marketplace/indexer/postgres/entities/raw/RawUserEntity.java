package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
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


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.users (data, login, id) VALUES (?, ?, ?) ON CONFLICT DO NOTHING")
public class RawUserEntity {
    @Id
    Long id;

    String login;

    @JdbcTypeCode(SqlTypes.JSON)
    RawAccount data;

    public static RawUserEntity of(RawAccount user) {
        return RawUserEntity.builder().id(user.getId()).login(user.getLogin()).data(user).build();
    }
}
