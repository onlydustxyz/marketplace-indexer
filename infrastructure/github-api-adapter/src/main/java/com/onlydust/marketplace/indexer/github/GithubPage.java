package com.onlydust.marketplace.indexer.github;

import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

@Slf4j
public class GithubPage<T> implements Iterator<T> {
    private final GithubHttpClient client;
    GithubPageLinks links;
    Deque<T> content = new ArrayDeque<>();
    Class<T[]> classType;

    public GithubPage(GithubHttpClient client, String path, Class<T[]> classType) {
        this.client = client;
        this.classType = classType;
        fetchNextPage(client.buildURI(path));
    }

    private void fetchNextPage(URI uri) {
        final var httpResponse = client.fetch(uri);
        switch (httpResponse.statusCode()) {
            case 200:
                links = httpResponse.headers().firstValue("Link").map(GithubPageLinks::of).orElse(new GithubPageLinks());
                content.addAll(asList(client.decode(httpResponse.body(), classType)));
                break;
            case 404:
                break;
            default:
                throw new NotFound("Unable to fetch github API:" + uri);
        }
    }

    @Override
    public boolean hasNext() {
        return !content.isEmpty() || !isNull(links.getNext());
    }

    @Override
    public T next() {
        if (content.isEmpty()) {
            fetchNextPage(links.getNext());
        }

        return content.poll();
    }
}