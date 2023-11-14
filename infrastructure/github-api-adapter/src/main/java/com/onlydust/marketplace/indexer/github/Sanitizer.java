package com.onlydust.marketplace.indexer.github;

import java.nio.charset.StandardCharsets;

public interface Sanitizer {
    static String sanitize(byte[] data) {
        return new String(data, StandardCharsets.UTF_8)
                .replace("\u0000", "\\u0000")
                .replace("\\\u0000", "\\u0000")
                ;
    }
}
