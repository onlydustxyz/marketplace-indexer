package com.onlydust.marketplace.indexer.github.adapters;

import com.onlydust.marketplace.indexer.github.GithubTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onlydust.com.marketplace.kernel.infrastructure.github.GithubAppJwtBuilder;

import java.util.Optional;


@Slf4j
@AllArgsConstructor
public class GithubAppJwtTokenProvider implements GithubTokenProvider {
    private GithubAppJwtBuilder jwtBuilder;

    @Override
    public Optional<String> accessToken() {
        return Optional.ofNullable(jwtBuilder.generateSignedJwtToken());
    }
}
