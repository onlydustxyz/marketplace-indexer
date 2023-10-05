package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.model.User;
import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageReaderStub;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexingServiceTest {
    final User anthony = RawStorageReaderStub.load("/github/users/anthony.json", User.class);
    final RawStorageReaderStub rawStorageReader = new RawStorageReaderStub();
    final RawStorageRepositoryStub rawStorageRepository = new RawStorageRepositoryStub();
    final IndexingService indexer = new IndexingService(rawStorageReader, rawStorageRepository);

    @BeforeEach
    void setup() throws IOException {
        rawStorageReader.initFromPath(Objects.requireNonNull(this.getClass().getResource("/github/users")).getPath());
    }

    @Test
    void should_index_user_from_its_id() {
        indexer.indexUserById(anthony.getId());
        assertCachedUsersAre(anthony);
    }

    private void assertCachedUsersAre(User... users) {
        assertThat(rawStorageRepository.users()).containsExactly(users);
    }
}
