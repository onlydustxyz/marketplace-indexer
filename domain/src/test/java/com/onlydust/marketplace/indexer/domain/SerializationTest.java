package com.onlydust.marketplace.indexer.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import onlydust.com.marketplace.indexer.domain.model.User;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {
    final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldDeserializeUserFromGithubResponse() throws Exception {
        final var resource = this.getClass().getResource("/github/users/Anthony.json");
        assert resource != null;

        final var fileContent = Files.readString(Path.of(resource.getPath()));

        final var user = mapper.readValue(fileContent, User.class);

        assertThat(user.getId()).isEqualTo(43467246);
        assertThat(user.getLogin()).isEqualTo("AnthonyBuisset");

        assertThat(mapper.readTree(fileContent)).isEqualTo(mapper.valueToTree(user));
    }
}
