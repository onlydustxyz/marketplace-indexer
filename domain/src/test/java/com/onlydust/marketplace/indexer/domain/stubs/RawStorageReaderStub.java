package com.onlydust.marketplace.indexer.domain.stubs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.model.User;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RawStorageReaderStub implements RawStorageReader {
    final List<User> users = new ArrayList<>();

    public static <T> T load(String path, Class<T> type) {
        final var inputStream = type.getResourceAsStream(path);
        try {
            return new ObjectMapper().readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> userById(Integer id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    public void initFromPath(String path) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        try (var files = Files.walk(Path.of(path))) {
            files.forEach(file -> {
                try {
                    if (file.toFile().isFile())
                        users.add(mapper.readValue(Files.readString(file), User.class));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
