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
public class BookingDtoRequest {
    Long id;
    @NotNull(message = "start date should not be empty")
    @FutureOrPresent(message = "start date should not be in past")
    LocalDateTime start;
    @NotNull(message = "end date should not be empty")
    @FutureOrPresent(message = "end date should not be in past")
    LocalDateTime end;
    Long itemId;
    Long bookerId;
    BookingStatus status;
}
