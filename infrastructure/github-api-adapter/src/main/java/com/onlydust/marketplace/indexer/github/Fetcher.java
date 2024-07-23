package com.onlydust.marketplace.indexer.github;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public interface Fetcher {
    HttpResponse<byte[]> fetch(HttpRequest request);
}
