package com.onlydust.marketplace.indexer.rest.api.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringAuthenticationContext implements AuthenticationContext {

    @Override
    public Authentication getAuthenticationFromContext() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
