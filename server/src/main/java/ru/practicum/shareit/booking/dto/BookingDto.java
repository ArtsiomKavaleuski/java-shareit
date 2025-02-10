package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemNameDto;
import ru.practicum.shareit.user.dto.UserNameDto;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private ItemNameDto item;
    private UserNameDto booker;
}
