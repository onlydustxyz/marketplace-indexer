package com.onlydust.marketplace.indexer.bootstrap.it.stubs;

import lombok.NonNull;
import org.springframework.core.task.TaskExecutor;

public class TaskExecutorStub implements TaskExecutor {
    @Override
    public void execute(@NonNull Runnable task) {
        task.run();
    }
}
