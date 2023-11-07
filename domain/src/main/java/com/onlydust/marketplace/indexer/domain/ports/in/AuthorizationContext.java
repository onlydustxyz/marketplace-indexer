package com.onlydust.marketplace.indexer.domain.ports.in;

public interface AuthorizationContext {
    void withAuthorization(String authorization, Runnable callback);
}
