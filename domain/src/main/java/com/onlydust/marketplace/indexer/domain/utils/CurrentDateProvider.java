package com.onlydust.marketplace.indexer.domain.utils;


import java.time.ZonedDateTime;

public class CurrentDateProvider {
    static DateProvider instance = ZonedDateTime::now;

    public static ZonedDateTime now() {
        return instance.now();
    }

    public static void set(DateProvider dateProvider) {
        instance = dateProvider;
    }

    public static void reset() {
        instance = ZonedDateTime::now;
    }
}
