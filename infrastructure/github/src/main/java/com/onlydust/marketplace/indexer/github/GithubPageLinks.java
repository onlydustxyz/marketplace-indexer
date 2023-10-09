package com.onlydust.marketplace.indexer.github;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.HashMap;
import java.util.regex.Pattern;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class GithubPageLinks {
    static Pattern PATTERN = Pattern.compile("<(.*)>; rel=\"(first|last|next|prev)\"");

    String prev;
    String next;
    String first;
    String last;

    public static GithubPageLinks of(String header) {
        final var links = new HashMap<String, String>();

        for (String link : header.split(",")) {
            final var match = PATTERN.matcher(link);
            if (match.find()) {
                links.put(match.group(2), match.group(1));
            }
        }

        return new GithubPageLinks(links.get("prev"), links.get("next"), links.get("first"), links.get("last"));
    }
}
