package com.onlydust.marketplace.indexer.rest.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.models.raw.RawEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventsInbox;
import com.onlydust.marketplace.indexer.rest.github.security.GithubSignatureVerifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@AllArgsConstructor
@Slf4j
public class GithubWebhookRestApi {
    private static final String X_GITHUB_EVENT = "X-GitHub-Event";
    private static final String X_HUB_SIGNATURE_256 = "X-Hub-Signature-256";
    private final Config config;
    private final EventsInbox inbox;
    private final ObjectMapper objectMapper;

    @PostMapping("/github-app/webhook")
    public ResponseEntity<Void> consumeWebhook(final @RequestBody byte[] payload,
                                               final @RequestHeader(X_GITHUB_EVENT) String type,
                                               final @RequestHeader(X_HUB_SIGNATURE_256) String signature) throws IOException {
        GithubSignatureVerifier.validateWebhook(payload, config.secret, signature);

        inbox.push(RawEvent.of(type, objectMapper.readTree(payload)));
        return ResponseEntity.ok().build();
    }

    @Data
    public static class Config {
        public String secret;
    }
}
