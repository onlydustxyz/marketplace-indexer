package com.onlydust.marketplace.indexer.github;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.net.URI;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class GithubPageLinks {
    static Pattern PATTERN = Pattern.compile("<(.*)>; rel=\"(first|last|next|prev)\"");

    URI prev;
    URI next;
    URI first;
    URI last;

    public static GithubPageLinks of(String header) {
        final var links = new HashMap<String, String>();

        for (String link : header.split(",")) {
            final var match = PATTERN.matcher(link);
            if (match.find()) {
                links.put(match.group(2), match.group(1));
            }
        }

        return new GithubPageLinks(
                Optional.ofNullable(links.get("prev")).map(URI::create).orElse(null),
                Optional.ofNullable(links.get("next")).map(URI::create).orElse(null),
                Optional.ofNullable(links.get("first")).map(URI::create).orElse(null),
                Optional.ofNullable(links.get("last")).map(URI::create).orElse(null)
        );
    }
}
