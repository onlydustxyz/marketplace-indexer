package onlydust.com.marketplace.indexer.rest.api.adapter.authentication;

import org.springframework.security.core.Authentication;

public interface AuthenticationContext {

    Authentication getAuthenticationFromContext();
}
