package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User getUser(long id) {
        if (userRepository.getUser(id) == null) {
            throw new NotFoundException("User not found");
        }
        return userRepository.getUser(id);
    }

    @Override
    public User addUser(User user) throws ConflictException {
        if (userRepository.getUsers().stream().map(User::getEmail).toList().contains(user.getEmail())) {
            throw new ConflictException("E-mail уже используется");
        }
        return userRepository.addUser(user);
    }

    @Override
    public User updateUser(long id, User user) throws ConflictException {
        if (userRepository.getUsers().stream().filter(u -> u.getId() != id).map(User::getEmail).toList().contains(user.getEmail())) {
            throw new ConflictException("E-mail уже используется");
        }
        return userRepository.updateUser(id, user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }
}
