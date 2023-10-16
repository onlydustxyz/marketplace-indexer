package com.onlydust.marketplace.indexer.github;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class GithubPageLinksTest {
    @Test
    void should_decode_valid_first_page_header() {
        final var links = GithubPageLinks.of("""
                <https://api.github.com/repositories/498695724/pulls?state=all&page=2>; rel="next", <https://api.github.com/repositories/498695724/pulls?state=all&page=42>; rel="last"
                """);
        assertThat(links.getPrev()).isNull();
        assertThat(links.getNext()).isEqualTo(URI.create("https://api.github.com/repositories/498695724/pulls?state=all&page=2"));
        assertThat(links.getFirst()).isNull();
        assertThat(links.getLast()).isEqualTo(URI.create("https://api.github.com/repositories/498695724/pulls?state=all&page=42"));
    }

    @Test
    void should_decode_valid_mid_page_header() {
        final var links = GithubPageLinks.of("""
                <https://api.github.com/repositories/498695724/pulls?state=all&page=4>; rel="prev", <https://api.github.com/repositories/498695724/pulls?state=all&page=6>; rel="next", <https://api.github.com/repositories/498695724/pulls?state=all&page=42>; rel="last", <https://api.github.com/repositories/498695724/pulls?state=all&page=1>; rel="first"
                 """);
        assertThat(links.getPrev()).isEqualTo(URI.create("https://api.github.com/repositories/498695724/pulls?state=all&page=4"));
        assertThat(links.getNext()).isEqualTo(URI.create("https://api.github.com/repositories/498695724/pulls?state=all&page=6"));
        assertThat(links.getFirst()).isEqualTo(URI.create("https://api.github.com/repositories/498695724/pulls?state=all&page=1"));
        assertThat(links.getLast()).isEqualTo(URI.create("https://api.github.com/repositories/498695724/pulls?state=all&page=42"));
    }

    @Test
    void should_decode_valid_last_page_header() {
        final var links = GithubPageLinks.of("""
                <https://api.github.com/repositories/498695724/pulls?state=all&page=41>; rel="prev", <https://api.github.com/repositories/498695724/pulls?state=all&page=1>; rel="first"
                 """);
        assertThat(links.getPrev()).isEqualTo(URI.create("https://api.github.com/repositories/498695724/pulls?state=all&page=41"));
        assertThat(links.getNext()).isNull();
        assertThat(links.getFirst()).isEqualTo(URI.create("https://api.github.com/repositories/498695724/pulls?state=all&page=1"));
        assertThat(links.getLast()).isNull();
    }

}