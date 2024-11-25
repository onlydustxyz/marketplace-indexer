package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "users", schema = "indexer_raw")
@ToString
@SQLInsert(sql = "INSERT INTO indexer_raw.users (data, login, id) VALUES (?, ?, ?) ON CONFLICT DO NOTHING")
public class RawUserEntity {
    @Id
    final Long id;

    final String login;

    @JdbcTypeCode(SqlTypes.JSON)
    RawAccount data;

    public static RawUserEntity of(RawAccount user) {
        return RawUserEntity.builder()
                .id(user.getId())
                .login(user.getLogin())
                .data(user)
                .build();
    }
}
