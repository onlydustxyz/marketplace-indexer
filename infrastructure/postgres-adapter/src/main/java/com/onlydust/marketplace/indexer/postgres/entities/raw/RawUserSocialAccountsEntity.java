package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;

import java.util.List;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "user_social_accounts", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.user_social_accounts (data, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING")
public class RawUserSocialAccountsEntity {
    @Id
    Long userId;

    @JdbcTypeCode(SqlTypes.JSON)
    List<RawSocialAccount> data;

    public static RawUserSocialAccountsEntity of(Long userId, List<RawSocialAccount> socialAccounts) {
        return RawUserSocialAccountsEntity.builder().userId(userId).data(socialAccounts).build();
    }
}
