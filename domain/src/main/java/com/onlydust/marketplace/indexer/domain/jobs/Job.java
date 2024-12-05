package com.onlydust.marketplace.indexer.domain.jobs;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public abstract class Job implements Runnable {
    private static final ConcurrentMap<String, Boolean> runningJobs = new ConcurrentHashMap<>();

    private static void safeRun(String key, Runnable runnable) {
        try {
            MDC.put("job", key);
            if (lock(key)) runnable.run();
            else LOGGER.info("already running, skipped");
        } finally {
            unlock(key);
            LOGGER.info("Job finished");
            MDC.remove("job");
        }
    }

    private static boolean lock(final String key) {
        return runningJobs.putIfAbsent(key, true) == null;
    }

    private static void unlock(final String key) {
        runningJobs.remove(key);
    }

    @Override
    public void run() {
        safeRun(name(), this::execute);
    }

    protected abstract void execute();

    public abstract String name();
}
