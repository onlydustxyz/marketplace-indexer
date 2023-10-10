package com.onlydust.marketplace.indexer.rest.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.rest.github.security.GithubSignatureVerifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@AllArgsConstructor
@Slf4j
public class GithubWebhookRestApi {
    private static final String X_GITHUB_EVENT = "X-GitHub-Event";
    private static final String X_HUB_SIGNATURE_256 = "X-Hub-Signature-256";
    private final ObjectMapper objectMapper;
    private final Config config;

    @PostMapping("/github-app/webhook")
    public ResponseEntity<Void> consumeWebhook(final @RequestBody byte[] githubWebhookDTOBytes,
                                               final @RequestHeader(X_GITHUB_EVENT) String githubEventType,
                                               final @RequestHeader(X_HUB_SIGNATURE_256) String githubSha256Signature) {
        GithubSignatureVerifier.validateWebhook(githubWebhookDTOBytes, config.secret, githubSha256Signature);

        LOGGER.info("EventType = {}, body = {}", githubEventType, new String(githubWebhookDTOBytes, StandardCharsets.UTF_8));
        return ResponseEntity.ok().build();
    }

    @Data
    public static class Config {
        public String secret;
    }
}
