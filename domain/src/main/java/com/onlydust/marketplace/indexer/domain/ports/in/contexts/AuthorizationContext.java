package com.onlydust.marketplace.indexer.domain.ports.in.contexts;

public interface AuthorizationContext {
    void withAuthorization(String authorization, Runnable callback);
}
