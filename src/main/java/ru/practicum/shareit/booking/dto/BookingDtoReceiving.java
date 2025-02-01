package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoReceiving {
    Long id;
    @NotNull
    @FutureOrPresent
    LocalDateTime start;
    @NotNull
    @FutureOrPresent
    LocalDateTime end;
    Long itemId;
    Long bookerId;
    BookingStatus status;
}
