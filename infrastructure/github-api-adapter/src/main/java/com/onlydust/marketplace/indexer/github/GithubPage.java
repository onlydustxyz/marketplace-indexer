package com.onlydust.marketplace.indexer.github;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpResponse;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

@Slf4j
public class GithubPage<T> implements Iterator<T> {
    final Deque<T> content = new ArrayDeque<>();
    final Class<T[]> classType;
    private final GithubHttpClient client;
    GithubPageLinks links;

    public GithubPage(GithubHttpClient client, String path, Class<T[]> classType) {
        this.client = client;
        this.classType = classType;
        decodeResponse(client.fetch(path));
    }

    private void decodeResponse(HttpResponse<byte[]> httpResponse) {
        switch (httpResponse.statusCode()) {
            case 200:
                links = httpResponse.headers().firstValue("Link").map(GithubPageLinks::of).orElse(new GithubPageLinks());
                content.addAll(asList(client.decodeBody(httpResponse.body(), classType)));
                break;
            case 403, 404, 422:
                break;
            default:
                throw OnlyDustException.internalServerError("Received incorrect status (" + httpResponse.statusCode() + ") when fetching github API");
        }
    }

    @Override
    public boolean hasNext() {
        return !content.isEmpty() || !isNull(links.getNext());
    }

    @Override
    public T next() {
        if (content.isEmpty()) {
            decodeResponse(client.fetch(links.getNext()));
        }

        return content.poll();
    }
}