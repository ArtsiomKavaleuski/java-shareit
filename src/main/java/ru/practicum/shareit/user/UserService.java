package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    Collection<User> getUsers();

    User getUser(long id);

    User addUser(User user) throws Exception;

    User updateUser(long id, User user) throws Exception;

    void deleteUser(long id);
}
