package com.onlydust.marketplace.indexer.api.client.adapters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlydust.marketplace.indexer.api.client.ApiHttpClient;
import com.onlydust.marketplace.indexer.domain.ports.out.IndexingObserver;
import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
public class IndexingObserverAdapter implements IndexingObserver {
    private final ApiHttpClient httpClient;

    @Override
    public void onNewContributions(Set<Long> repoIds) {
        httpClient.sendRequest("/api/v1/events/on-contributions-change", HttpMethod.POST, new Request(repoIds), Void.class);
    }

    record Request(@JsonProperty("repoIds") Set<Long> repoIds) implements Serializable {
    }
}
