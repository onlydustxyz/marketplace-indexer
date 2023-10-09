package com.onlydust.marketplace.indexer.rest.api.authentication.api_key;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
public class ApiKeyAuthenticationService {
    private static final String AUTH_TOKEN_HEADER_NAME = "Api-Key";

    private final Config config;

    public Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if (apiKey == null || !apiKey.equals(config.apiKey)) {
            throw new BadCredentialsException("Invalid API Key");
        }

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }

    @Data
    public static class Config {
        String apiKey;
    }
}
