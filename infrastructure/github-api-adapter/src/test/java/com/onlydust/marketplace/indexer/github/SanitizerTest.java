package com.onlydust.marketplace.indexer.github;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SanitizerTest {
    @Test
    public void should_sanitize_content() {
        final var sanitized = Sanitizer.sanitize("hello\\\\u0000\n\\u0000\\u0000\\u0000\\u0000world".getBytes());
        assertThat(sanitized).isEqualTo("hello\\u0000\nworld");
    }
}
