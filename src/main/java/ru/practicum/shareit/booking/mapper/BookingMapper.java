package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }

    public static Booking toBooking(BookingDtoRequest bookingDtoRequest, Item item, User booker) {
        Booking booking = new Booking();
        if (bookingDtoRequest.getId() != null) {
            booking.setId(bookingDtoRequest.getId());
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(bookingDtoRequest.getStart());
        booking.setEnd(bookingDtoRequest.getEnd());
        booking.setStatus(bookingDtoRequest.getStatus());
        return booking;
    }

    public static List<BookingDto> toBookingDtoList(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public static BookingDtoOwner toBookingDtoOwner(Booking booking) {
        return new BookingDtoOwner(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }
}
