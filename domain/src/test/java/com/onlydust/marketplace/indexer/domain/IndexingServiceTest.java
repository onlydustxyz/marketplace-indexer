package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.model.SocialAccount;
import com.onlydust.marketplace.indexer.domain.model.User;
import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexingServiceTest {
    final User anthony = RawStorageRepositoryStub.load("/github/users/anthony.json", User.class);
    final SocialAccount[] anthonySocialAccounts = RawStorageRepositoryStub.load("/github/users/anthony_social_accounts.json", SocialAccount[].class);
    final RawStorageRepositoryStub rawStorageReader = new RawStorageRepositoryStub();
    final RawStorageRepositoryStub rawStorageRepository = new RawStorageRepositoryStub();
    final IndexingService indexer = new IndexingService(rawStorageReader, rawStorageRepository);

    @BeforeEach
    void setup() throws IOException {
        rawStorageReader.feedWith(anthony);
        rawStorageReader.feedWith(anthony.getId(), anthonySocialAccounts);
    }

    @Test
    void should_index_user_from_its_id() {
        indexer.indexUserById(anthony.getId());
        assertCachedUsersAre(anthony);
        assertCachedUserSocialAccountsAre(anthony.getId(), anthonySocialAccounts);
    }

    private void assertCachedUsersAre(User... users) {
        assertThat(rawStorageRepository.users()).containsExactly(users);
    }

    private void assertCachedUserSocialAccountsAre(Integer userId, SocialAccount... socialAccounts) {
        assertThat(rawStorageRepository.userSocialAccounts(userId)).containsExactly(socialAccounts);
    }
}
