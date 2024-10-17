package com.onlydust.marketplace.indexer.domain.models.exposition;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContributionUUIDTest {

    @Test
    void ofLong() {
        assertThat(ContributionUUID.of(123L).toString()).isEqualTo("dcc87657-8e6f-3095-8cca-2cf98457104b");
    }

    @Test
    void ofString() {
        assertThat(ContributionUUID.of("foo").toString()).isEqualTo("d657f8eb-ad4f-3d7d-88ea-4c752dd6ccd2");
    }
}