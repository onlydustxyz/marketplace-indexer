package com.onlydust.marketplace.indexer.github.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RateLimitResponse(Rate rate) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Rate(Integer remaining, Integer reset) {
    }
}
