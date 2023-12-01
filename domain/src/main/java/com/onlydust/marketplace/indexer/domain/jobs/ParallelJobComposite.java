package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

public class ParallelJobComposite extends Job {
    private final Executor executor;
    private final Collection<Job> jobs;

    public ParallelJobComposite(final Executor executor, final Collection<Job> jobs) {
        this.executor = executor;
        this.jobs = jobs;
    }

    @Override
    public void execute() {
        final var tasks = jobs.stream().map(j -> new FutureTask<>(j, null)).toList();
        tasks.forEach(executor::execute);
        tasks.forEach(task -> {
            try {
                task.get();
            } catch (Exception e) {
                throw OnlyDustException.internalServerError("Error running job %s".formatted(name()), e);
            }
        });
    }

    @Override
    public String name() {
        return "[%s]".formatted(String.join(", ", jobs.stream().map(Job::name).toList()));
    }
}
