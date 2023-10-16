package com.onlydust.marketplace.indexer.rest.api.authentication.api_key;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
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
