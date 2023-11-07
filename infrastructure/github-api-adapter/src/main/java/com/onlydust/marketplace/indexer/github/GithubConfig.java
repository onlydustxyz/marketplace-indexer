package com.onlydust.marketplace.indexer.github;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class GithubConfig {
    private String baseUri;
    private String personalAccessToken;
    private Integer maxRetries;
    private Integer retryInterval;
}