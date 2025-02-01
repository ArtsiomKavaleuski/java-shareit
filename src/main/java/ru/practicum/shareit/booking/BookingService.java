package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReceiving;

import java.util.List;

public interface BookingService {
    Booking addBooking(Long userId, BookingDtoReceiving bookingDtoReceiving);
    Booking approveBooking(Long userId, Long bookingId, String isApproved);
    Booking getBooking(Long userId, Long bookingId);
    List<Booking> getBookingListByBooker(Long userId, String state);
    List<Booking> getBookingListByOwner(Long userId, String state);
}
