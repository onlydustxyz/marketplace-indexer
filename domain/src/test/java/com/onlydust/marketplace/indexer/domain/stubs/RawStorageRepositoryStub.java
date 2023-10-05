package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.model.User;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RawStorageRepositoryStub implements RawStorageRepository {
    final List<User> users = new ArrayList<>();

    @Override
    public Optional<User> userById(Integer id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    @Override
    public void save(User user) {
        users.add(user);
    }

    public List<User> users() {
        return users;
    }
}
