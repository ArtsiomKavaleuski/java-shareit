package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private long itemId;
    @FutureOrPresent(message = "Start date should not be in past")
    @NotNull(message = "Start date is obligatory")
    private LocalDateTime start;
    @Future(message = "End date should not be in past")
    @NotNull(message = "End date is obligatory")
    private LocalDateTime end;
}