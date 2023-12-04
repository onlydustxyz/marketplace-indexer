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
import com.onlydust.marketplace.indexer.domain.jobs.EventsInboxJob;
import com.onlydust.marketplace.indexer.rest.github.GithubWebhookRestApi;
import com.onlydust.marketplace.indexer.rest.github.security.GithubSignatureVerifier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.testcontainers.utility.MountableFile.forClasspathResource;

@ActiveProfiles({"it", "local", "api", "github"})
@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ApplicationIT.class)
@Testcontainers
@Slf4j
@DirtiesContext
@Import(SwaggerConfiguration.class)
@EnableWireMock({
        @ConfigureWireMock(name = "github", property = "infrastructure.github.baseUri"),
        @ConfigureWireMock(name = "api", property = "infrastructure.api-client.baseUri")
})
public class IntegrationTest {
    @Container
    static final PostgreSQLContainer postgresSQLContainer =
            new PostgreSQLContainer<>("postgres:14.3-alpine")
                    .withDatabaseName("marketplace_db")
                    .withUsername("test")
                    .withPassword("test")
                    .waitingFor(Wait.forLogMessage(".*PostgreSQL init process complete; ready for start up.*", 1))
                    .withCopyFileToContainer(
                            forClasspathResource("db_init_script/"), "/docker-entrypoint-initdb.d");

    protected final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @InjectWireMock("github")
    protected WireMockServer githubWireMockServer;
    @InjectWireMock("api")
    protected WireMockServer apiWireMockServer;
    @LocalServerPort
    int port;
    @Autowired
    WebTestClient client;
    @Autowired
    GithubWebhookRestApi.Config config;
    @Autowired
    EventsInboxJob eventsInboxJob;

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

    protected WebTestClient.ResponseSpec postEvent(final String event, String eventTypeHeader) {
        return client.post().uri(getApiURI("/github-app/webhook")).header("X-GitHub-Event", eventTypeHeader)
                .header("X-Hub-Signature-256", "sha256=" + GithubSignatureVerifier.hmac(event.getBytes(),
                        config.secret))
                .bodyValue(event)
                .exchange();
    }

    protected void processEvent(String event, String installation) {
        postEvent(event, installation).expectStatus().isOk();
        eventsInboxJob.run();
    }
}
