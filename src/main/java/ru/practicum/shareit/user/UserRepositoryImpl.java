package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static long lastId = 0;
    private static final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(long id, User user) {
        User updatedUser = users.get(id);
        updatedUser.setName(user.getName());
        updatedUser.setEmail(user.getEmail());
        return users.put(id, updatedUser);
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    private long generateId() {
        return lastId++;
    }
}
