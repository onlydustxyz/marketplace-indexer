package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.UserIndexingJobTrigger;

import java.util.List;

public interface UserIndexingJobTriggerRepository {
    List<UserIndexingJobTrigger> list();
}
