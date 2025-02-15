package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getUsers();

    User getUser(Long id);

    UserDto addUser(UserDto userDto);

    User updateUser(Long id, User user);

    void delete(Long id);
}
