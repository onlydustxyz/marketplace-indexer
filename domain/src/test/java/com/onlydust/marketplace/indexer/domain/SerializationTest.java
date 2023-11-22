package com.onlydust.marketplace.indexer.domain;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {
    final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void should_deserialize_user_from_github_response() throws Exception {
        mapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);

        final var resource = this.getClass().getResource("/github/users/anthony.json");
        assert resource != null;

        final var fileContent = Files.readString(Path.of(resource.getPath()));

        final var user = mapper.readValue(fileContent, RawAccount.class);

        assertThat(user.getId()).isEqualTo(43467246);
        assertThat(user.getLogin()).isEqualTo("AnthonyBuisset");

        assertThat(mapper.readTree(fileContent)).isEqualTo(mapper.valueToTree(user));
    }
}
