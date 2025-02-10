package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User not found"));
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("E-mail уже используется");
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.fromUserDto(userDto)));
    }

    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        User updatedUser = getUser(id);
        if (userRepository.findAll().stream()
                .filter(u -> !Objects.equals(u.getId(), id)).map(User::getEmail)
                .toList().contains(user.getEmail())) {
            throw new ConflictException("E-mail уже используется");
        }
        if (user.getName() != null) updatedUser.setName(user.getName());
        if (user.getEmail() != null) updatedUser.setEmail(user.getEmail());
        return userRepository.save(updatedUser);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new NotFoundException("User not found");
        }
    }

}
