package com.onlydust.marketplace.indexer.rest.api.authentication;

import com.onlydust.marketplace.indexer.rest.api.authentication.api_key.ApiKeyAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityAdapter extends WebSecurityConfigurerAdapter {
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors()
                .and().csrf().disable()
                .authorizeRequests().anyRequest().permitAll()
                .and().addFilterBefore(apiKeyAuthenticationFilter, AnonymousAuthenticationFilter.class)
        ;
    }

}
