package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional
    public User addUser(User user) throws ConflictException {
        if (userRepository.findAll().stream().map(User::getEmail).toList().contains(user.getEmail())) {
            throw new ConflictException("E-mail уже используется");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(long id, User user) throws ConflictException {
        if (userRepository.findAll().stream()
                .filter(u -> u.getId() != id).map(User::getEmail)
                .toList().contains(user.getEmail())) {
            throw new ConflictException("E-mail уже используется");
        }
        User updatedUser = getUser(id);
        if (user.getName() != null) updatedUser.setName(user.getName());
        if (user.getEmail() != null) updatedUser.setEmail(user.getEmail());
        return userRepository.save(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
