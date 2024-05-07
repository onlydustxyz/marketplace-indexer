package com.onlydust.marketplace.indexer.rest.api.authentication.api_key;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
public class ApiKeyAuthenticationFilter extends GenericFilterBean {
    private final ApiKeyAuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        authenticationService.getAuthentication((HttpServletRequest) request).ifPresent(
                authentication -> SecurityContextHolder.getContext().setAuthentication(authentication)
        );

        filterChain.doFilter(request, response);
    }
}
