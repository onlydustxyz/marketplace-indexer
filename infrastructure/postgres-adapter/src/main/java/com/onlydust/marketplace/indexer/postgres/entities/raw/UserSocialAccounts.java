package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "user_social_accounts", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SQLInsert(sql = "INSERT INTO indexer_raw.user_social_accounts (data, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING")
public class UserSocialAccounts {
    @Id
    Long userId;

    @Type(type = "jsonb")
    List<RawSocialAccount> data;

    public static UserSocialAccounts of(Long userId, List<RawSocialAccount> socialAccounts) {
        return UserSocialAccounts.builder().userId(userId).data(socialAccounts).build();
    }
}
