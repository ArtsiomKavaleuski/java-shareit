package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private UserService userService;
    private UserRepository userRepository;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = User.builder()
                .id(1L)
                .name("Test")
                .email("Email@test.com")
                .build();
        userDto = UserMapper.toUserDto(user);

        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    void getUsers() {
        List<UserDto> result = userService.getUsers().stream()
                .map(UserMapper::toUserDto)
                .toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getId(), userDto.getId());
        Assertions.assertEquals(result.getFirst().getName(), userDto.getName());
        Assertions.assertEquals(result.getFirst().getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void addUser() {
        UserDto result = userService.addUser(userDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), userDto.getId());
        Assertions.assertEquals(result.getName(), userDto.getName());
        Assertions.assertEquals(result.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void addUserAlreadyExists() {
        userService.addUser(userDto);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        Assertions.assertThrows(ConflictException.class, () -> userService.addUser(userDto));
        verify(userRepository, times(1)).save(any());
    }


    @Test
    public void getUserNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(1L));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void updateUser() {
        User result = userService.updateUser(user.getId(), user);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), userDto.getId());
        Assertions.assertEquals(result.getName(), userDto.getName());
        Assertions.assertEquals(result.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void updateUserNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUser(3L, user));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void updateUserAlreadyExists() {
        userService.addUser(userDto);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        Assertions.assertThrows(ConflictException.class, () -> userService.updateUser(userDto.getId(), user));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getUser() {
        User result = userService.getUser(user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), userDto.getId());
        Assertions.assertEquals(result.getName(), userDto.getName());
        Assertions.assertEquals(result.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteUser() {
        userService.delete(user.getId());
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void deleteUserNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.delete(1L));
        verify(userRepository, times(1)).findById(any());
    }
}
