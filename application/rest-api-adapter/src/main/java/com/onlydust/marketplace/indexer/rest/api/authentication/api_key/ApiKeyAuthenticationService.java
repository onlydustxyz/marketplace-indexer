package com.onlydust.marketplace.indexer.rest.api.authentication.api_key;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Optional;

@AllArgsConstructor
public class ApiKeyAuthenticationService {
    private static final String AUTH_TOKEN_HEADER_NAME = "Api-Key";

    private final Config config;

    public Optional<Authentication> getAuthentication(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTH_TOKEN_HEADER_NAME))
                .filter(apiKey -> apiKey.equals(config.apiKey))
                .map(apiKey -> new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES));
    }

    @Data
    public static class Config {
        String apiKey;
    }
}
