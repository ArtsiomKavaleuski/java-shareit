package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @PostMapping
    public User addUser(@Validated @RequestBody User user) throws Exception {
        return userService.addUser(user);
    }

    @PatchMapping("{id}")
    public User updateUser(@PathVariable long id, @RequestBody User user) throws Exception {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }

}
