package com.onlydust.marketplace.indexer.github;

import java.nio.charset.StandardCharsets;

public interface Sanitizer {
    static String sanitize(String text) {
        return text
            .replace("\u0000", "\\u0000")
            .replace("\\\u0000", "\\u0000");
    }

    static String sanitize(byte[] data) {
        return sanitize(new String(data, StandardCharsets.UTF_8));
    }
}
