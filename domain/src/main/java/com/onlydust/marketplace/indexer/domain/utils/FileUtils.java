package com.onlydust.marketplace.indexer.domain.utils;

import java.io.File;
import java.util.Optional;

public interface FileUtils {
    static Optional<String> fileExtension(String filePath) {
        final var fileName = new File(filePath).getName();
        return Optional.of(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf('.') + 1))
                .map(String::toLowerCase);
    }
}
