package com.onlydust.marketplace.indexer.github;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SanitizerTest {
    @Test
    public void should_sanitize_bytes() {
        assertThat(Sanitizer.sanitize("hello\u0000world".getBytes())).isEqualTo("hello\\u0000world");
        assertThat(Sanitizer.sanitize("hello\\u0000world".getBytes())).isEqualTo("hello\\u0000world");
        assertThat(Sanitizer.sanitize("hello\u0000\u0000\\u0000world".getBytes())).isEqualTo("hello\\u0000\\u0000\\u0000world");

        assertThat(Sanitizer.sanitize("hello\u0000world")).isEqualTo("hello\\u0000world");
        assertThat(Sanitizer.sanitize("hello\\u0000world")).isEqualTo("hello\\u0000world");
        assertThat(Sanitizer.sanitize("hello\u0000\u0000\\u0000world")).isEqualTo("hello\\u0000\\u0000\\u0000world");
    }
}
