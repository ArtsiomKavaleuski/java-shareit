package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    Booking addBooking(Long userId, BookingDtoRequest bookingDtoRequest);
    Booking approveBooking(Long userId, Long bookingId, String isApproved);
    Booking getBooking(Long userId, Long bookingId);
    List<Booking> getBookingListByBooker(Long userId, String state);
    List<Booking> getBookingListByOwner(Long userId, String state);
}
