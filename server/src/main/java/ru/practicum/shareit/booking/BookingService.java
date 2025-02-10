package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingRequestDto bookingRequestDto);

    BookingDto approveBooking(Long userId, Long bookingId, Boolean isApproved);

    BookingDto getBooking(Long userId, Long bookingId);

    Collection<BookingDto> getBookingListByBooker(Long userId, String state);

    Collection<BookingDto> getBookingListByOwner(Long userId, String state);
}