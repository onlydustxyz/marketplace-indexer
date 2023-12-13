package com.onlydust.marketplace.indexer.bootstrap.it;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import com.onlydust.marketplace.indexer.bootstrap.ApplicationIT;
import com.onlydust.marketplace.indexer.bootstrap.configuration.SwaggerConfiguration;
import com.onlydust.marketplace.indexer.domain.jobs.InstallationEventsInboxJob;
import com.onlydust.marketplace.indexer.domain.jobs.OtherEventsInboxJob;
import com.onlydust.marketplace.indexer.postgres.entities.EventsInboxEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.EventsInboxEntityRepository;
import com.onlydust.marketplace.indexer.rest.github.GithubWebhookRestApi;
import com.onlydust.marketplace.indexer.rest.github.security.GithubSignatureVerifier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.testcontainers.utility.MountableFile.forClasspathResource;

@ActiveProfiles({"it", "local", "api", "github"})
@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ApplicationIT.class)
@Testcontainers
@Slf4j
@Import(SwaggerConfiguration.class)
@EnableWireMock({
        @ConfigureWireMock(name = "github", property = "infrastructure.github.baseUri"),
        @ConfigureWireMock(name = "githubForApp", property = "infrastructure.github-for-app.base-uri", stubLocation = "github"),
        @ConfigureWireMock(name = "api", property = "infrastructure.api-client.baseUri")
})
public class IntegrationTest {
    static final PostgreSQLContainer postgresSQLContainer = new PostgreSQLContainer<>("postgres:14.3-alpine")
            .withDatabaseName("marketplace_db")
            .withUsername("test")
            .withPassword("test")
            .waitingFor(Wait.forLogMessage(".*PostgreSQL init process complete; ready for start up.*", 1))
            .withCopyFileToContainer(forClasspathResource("db_init_script/"), "/docker-entrypoint-initdb.d")
            .withCopyFileToContainer(forClasspathResource("scripts/"), "/scripts");

    protected final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();
    @InjectWireMock("github")
    protected WireMockServer githubWireMockServer;
    @InjectWireMock("githubForApp")
    protected WireMockServer githubForAppWireMockServer;
    @InjectWireMock("api")
    protected WireMockServer apiWireMockServer;
    @LocalServerPort
    int port;
    @Autowired
    WebTestClient client;
    @Autowired
    GithubWebhookRestApi.Config config;
    @Autowired
    InstallationEventsInboxJob installationEventsInboxJob;
    @Autowired
    OtherEventsInboxJob otherEventsInboxJob;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    EventsInboxEntityRepository eventsInboxEntityRepository;

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        if (!postgresSQLContainer.isRunning()) {
            postgresSQLContainer.start();
        }
        assertThat(postgresSQLContainer.execInContainer("psql", "-d", "marketplace_db", "-U", "test", "-f", "/scripts/clean.sql").getExitCode()).isEqualTo(0);
    }

    @DynamicPropertySource
    static void updateProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgresSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgresSQLContainer::getUsername);
    }

    @BeforeEach
    void setup() {
        mapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
    }

    protected URI getApiURI(final String path) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path(path)
                .build()
                .toUri();
    }

    protected WebTestClient.ResponseSpec put(final String path) {
        return client.put().uri(getApiURI(path)).header("Api-Key", "BACKEND_API_KEY").exchange();
    }

    protected WebTestClient.ResponseSpec put(final String path, Map<String, String> headers) {
        final var request = client.put().uri(getApiURI(path)).header("Api-Key", "BACKEND_API_KEY");
        headers.forEach(request::header);
        return request.exchange();
    }

    protected WebTestClient.ResponseSpec post(final String path, final String body) {
        return client.post()
                .uri(getApiURI(path))
                .header("Api-Key", "BACKEND_API_KEY")
                .contentType(APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    private WebTestClient.ResponseSpec postEvent(final String eventTypeHeader, final String event) {
        return client.post().uri(getApiURI("/github-app/webhook")).header("X-GitHub-Event", eventTypeHeader)
                .header("X-Hub-Signature-256", "sha256=" + GithubSignatureVerifier.hmac(event.getBytes(),
                        config.secret))
                .bodyValue(event)
                .exchange();
    }

    protected void processEventsFromPaths(final String eventType, final String... paths) {
        Arrays.stream(paths)
                .map(path -> {
                    try {
                        return Files.readString(Paths.get(this.getClass().getResource(path).toURI()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(event -> postEvent(eventType, event).expectStatus().isOk());
        installationEventsInboxJob.run();
        otherEventsInboxJob.run();
    }

    protected void assertAllEventsAreProcessed(String type) {
        final var events = eventsInboxEntityRepository.findAll().stream().filter(e -> e.getType().equals(type)).toList();
        assertThat(events).isNotEmpty();
        assertThat(events.stream().allMatch(e -> e.getStatus().equals(EventsInboxEntity.Status.PROCESSED)));
    }
}
