package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReceiving;
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

    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        Booking booking = new Booking();
        if (bookingDto.getId() != null) {
            booking.setId(bookingDto.getId());
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static Booking toBooking(BookingDtoReceiving bookingDtoReceiving, Item item, User booker) {
        Booking booking = new Booking();
        if (bookingDtoReceiving.getId() != null) {
            booking.setId(bookingDtoReceiving.getId());
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(bookingDtoReceiving.getStart());
        booking.setEnd(bookingDtoReceiving.getEnd());
        booking.setStatus(bookingDtoReceiving.getStatus());
        return booking;
    }

    public static List<BookingDto> toBookingDtoList(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
