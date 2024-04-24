package com.onlydust.marketplace.indexer.domain.models.raw;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

class RawPullRequestDiffTest {
    @SneakyThrows
    @Test
    void should_parse_diff() {
        // Given
        final var diff = requireNonNull(getClass().getResourceAsStream("/github/repos/marketplace-backend/pulls/38.diff")).readAllBytes();

        // When
        final var raw = RawPullRequestDiff.of(diff);

        // Then
        assertThat(raw.getModifiedFiles()).hasSize(37);

        final var modifiedFile = raw.getModifiedFiles().stream()
                .filter(f -> f.path().equals("accounting-domain/src/main/java/onlydust/com/marketplace/accounting/domain/model/Network.java"))
                .findFirst().orElseThrow();
        assertThat(modifiedFile.linesAdded()).isEqualTo(1);
        assertThat(modifiedFile.linesDeleted()).isEqualTo(1);

        final var addedFile = raw.getModifiedFiles().stream()
                .filter(f -> f.path().equals("accounting-domain/src/main/java/onlydust/com/marketplace/accounting/domain/port/in/BlockchainFacadePort.java"))
                .findFirst().orElseThrow();
        assertThat(addedFile.linesAdded()).isEqualTo(9);
        assertThat(addedFile.linesDeleted()).isEqualTo(0);

        final var deletedFile = raw.getModifiedFiles().stream()
                .filter(f -> f.path().equals("kernel/src/main/java/onlydust/com/marketplace/kernel/model/blockchain/aptos/TransactionHash.java"))
                .findFirst().orElseThrow();
        assertThat(deletedFile.linesAdded()).isEqualTo(0);
        assertThat(deletedFile.linesDeleted()).isEqualTo(14);
    }
}