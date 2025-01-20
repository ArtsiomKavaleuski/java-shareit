package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserRepository {
    Collection<User> getUsers();

    User getUser(long id);

    User addUser(User user);

    User updateUser(long id, User user);

    void deleteUser(long id);
}
