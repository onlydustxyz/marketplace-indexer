package com.onlydust.marketplace.indexer.domain.jobs;

import java.util.Arrays;

public class SequentialJobComposite extends Job {
    private final Job[] jobs;

    public SequentialJobComposite(Job... jobs) {
        this.jobs = jobs;
    }

    @Override
    public void execute() {
        Arrays.stream(jobs).forEach(Job::run);
    }

    @Override
    public String name() {
        return "[%s]".formatted(Arrays.stream(jobs).map(Job::name).reduce("%s -> %s"::formatted).orElse(""));
    }
}
