package com.onlydust.marketplace.indexer.rest.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class GithubWebhookRestApi {
    private static final String X_GITHUB_EVENT = "X-GitHub-Event";
    private static final String X_HUB_SIGNATURE_256 = "X-Hub-Signature-256";
    private final ObjectMapper objectMapper;

    @PostMapping("/github-app/webhook")
    public ResponseEntity<Void> consumeWebhook(final @RequestBody byte[] githubWebhookDTOBytes,
                                               final @RequestHeader(X_GITHUB_EVENT) String githubEventType,
                                               final @RequestHeader(X_HUB_SIGNATURE_256) String githubSha256Signature) {
        try {
            LOGGER.info("EventType = {}", githubEventType);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOGGER.error("Error while consuming github webhook", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
