package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.AccountStorage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserExposer implements Exposer<CleanAccount> {
    AccountStorage accountStorage;

    @Override
    public void expose(CleanAccount user) {
        accountStorage.save(GithubAccount.of(user));
    }
}
