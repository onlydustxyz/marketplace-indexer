package com.onlydust.indexer.infrastructure.quartz.adapters;

import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.HashSet;

@Slf4j
@AllArgsConstructor
public class UserRefresherJob implements Job {
    private final UserIndexer userIndexer;


    @Override
    public void execute(JobExecutionContext context) {
        final var users = context.getJobDetail().getJobDataMap().get("users");
        if (users instanceof HashSet) {
            ((HashSet<?>) users).forEach(user -> userIndexer.indexUser((Long) user));
        }
    }
}
