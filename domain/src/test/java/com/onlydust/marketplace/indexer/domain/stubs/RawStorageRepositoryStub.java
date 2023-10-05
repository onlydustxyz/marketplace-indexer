package com.onlydust.marketplace.indexer.domain.stubs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.model.SocialAccount;
import com.onlydust.marketplace.indexer.domain.model.User;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageRepository;

import java.io.IOException;
import java.util.*;

public class RawStorageRepositoryStub implements RawStorageRepository {
    final List<User> users = new ArrayList<>();
    final Map<Integer, List<SocialAccount>> userSocialAccounts = new HashMap<>();

    public static <T> T load(String path, Class<T> type) {
        final var inputStream = type.getResourceAsStream(path);
        try {
            return new ObjectMapper().readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> userById(Integer userId) {
        return users.stream().filter(user -> user.getId().equals(userId)).findFirst();
    }

    @Override
    public Optional<List<SocialAccount>> userSocialAccountsById(Integer userId) {
        try {
            return Optional.of(userSocialAccounts.get(userId));
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    public void feedWith(User... users) {
        Arrays.stream(users).sequential().forEach(this::save);
    }

    public void feedWith(Integer userId, SocialAccount... socialAccounts) {
        this.userSocialAccounts.put(userId, Arrays.stream(socialAccounts).toList());
    }

    @Override
    public void save(User user) {
        users.add(user);
    }

    @Override
    public void save(Integer userId, List<SocialAccount> socialAccounts) {
        userSocialAccounts.put(userId, socialAccounts);
    }

    public List<User> users() {
        return users;
    }

    public List<SocialAccount> userSocialAccounts(Integer userId) {
        return userSocialAccounts.get(userId);
    }
}
