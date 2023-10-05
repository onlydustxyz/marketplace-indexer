package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.model.raw.RawUser;
import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IndexingServiceTest {
    final RawUser anthony = RawStorageRepositoryStub.load("/github/users/anthony.json", RawUser.class);
    final RawSocialAccount[] anthonyRawSocialAccounts = RawStorageRepositoryStub.load("/github/users/anthony_social_accounts.json", RawSocialAccount[].class);
    final RawStorageRepositoryStub rawStorageReader = new RawStorageRepositoryStub();
    final RawStorageRepositoryStub rawStorageRepository = new RawStorageRepositoryStub();
    final IndexingService indexer = new IndexingService(rawStorageReader, rawStorageRepository);

    @BeforeEach
    void setup() throws IOException {
        rawStorageReader.feedWith(anthony);
        rawStorageReader.feedWith(anthony.getId(), anthonyRawSocialAccounts);
    }

    @Test
    void should_index_user_from_its_id() {
        final var user = indexer.indexUserById(anthony.getId());

        assertThat(user.getId()).isEqualTo(anthony.getId());
        assertThat(user.getLogin()).isEqualTo(anthony.getLogin());
        assertThat(user.getRawSocialAccounts()).containsExactly(anthonyRawSocialAccounts);

        assertCachedUsersAre(anthony);
        assertCachedUserSocialAccountsAre(anthony.getId(), anthonyRawSocialAccounts);
    }

    @Test
    void should_throw_when_indexing_non_existing_items() {
        assertThatThrownBy(() -> {
            indexer.indexUserById(0);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User not found");

        assertCachedUsersAre();
    }

    private void assertCachedUsersAre(RawUser... rawUsers) {
        assertThat(rawStorageRepository.users()).containsExactly(rawUsers);
    }

    private void assertCachedUserSocialAccountsAre(Integer userId, RawSocialAccount... rawSocialAccounts) {
        assertThat(rawStorageRepository.userSocialAccounts(userId)).containsExactly(rawSocialAccounts);
    }
}
