package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    private String name;
    @Email
    @UniqueElements
    private String email;
}
