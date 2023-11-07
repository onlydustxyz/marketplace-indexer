package com.onlydust.marketplace.indexer.github;

import com.onlydust.marketplace.indexer.domain.ports.in.AuthorizationContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


@Slf4j
public class GithubAuthorizationContext implements AuthorizationContext, GithubTokenProvider {
    String accessToken;

    @Override
    public void withAuthorization(final String authorization, Runnable callback) {
        try {
            if (authorization != null && authorization.startsWith("Bearer ghp_"))
                this.accessToken = authorization.replace("Bearer ", "");
            callback.run();
        } finally {
            this.accessToken = null;
        }
    }

    @Override
    public Optional<String> accessToken() {
        return Optional.ofNullable(accessToken);
    }
}
