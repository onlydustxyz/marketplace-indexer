package com.onlydust.marketplace.indexer.github;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.AuthorizationContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


@Slf4j
public class GithubAuthorizationContext implements AuthorizationContext, GithubTokenProvider {
    private final ThreadLocal<String> accessToken = new ThreadLocal<>();

    @Override
    public void withAuthorization(final String authorization, Runnable callback) {
        try {
            if (authorization != null && authorization.startsWith("Bearer ghp_"))
                this.accessToken.set(authorization.replace("Bearer ", ""));
            callback.run();
        } finally {
            this.accessToken.remove();
        }
    }

    @Override
    public Optional<String> accessToken() {
        return Optional.ofNullable(accessToken.get());
    }
}
