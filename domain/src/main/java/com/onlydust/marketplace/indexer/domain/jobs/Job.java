package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public abstract class Job implements Runnable {
    static final ConcurrentMap<String, Boolean> runningJobs = new ConcurrentHashMap<>();

    @Override
    public void run() {
        if (!lock()) {
            LOGGER.info("Job {} is already running, skipping", name());
            return;
        }

        try {
            LOGGER.info("Starting job {}", name());
            execute();
        } catch (Exception e) {
            LOGGER.error("Job {} failed", name(), OnlyDustException.internalServerError("Job " + name() + " failed", e));
        } finally {
            unlock();
        }
    }

    private boolean lock() {
        return runningJobs.putIfAbsent(name(), true) == null;
    }

    private void unlock() {
        runningJobs.remove(name());
        LOGGER.info("Job {} finished", name());
    }

    protected abstract void execute();

    public abstract String name();
}
