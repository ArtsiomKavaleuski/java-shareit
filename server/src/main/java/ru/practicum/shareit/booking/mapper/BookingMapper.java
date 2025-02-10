package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemNameDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserNameDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingDto toBookingRequestDto(Booking booking) {
        return BookingDto.builder().id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemNameDto.builder().id(booking.getItem().getId()).name(booking.getItem().getName()).build())
                .booker(UserNameDto.builder().id(booking.getBooker().getId()).name(booking.getBooker().getName()).build())
                .bookingStatus(booking.getBookingStatus())
                .build();
    }

    public static BookingInfoDto toBookingItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingInfoDto.builder().id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Collection<BookingDto> toListBookingRequestDto(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingRequestDto).collect(Collectors.toList());
    }

    public static Booking toBooking(BookingRequestDto bookingRequestDto, User user, Item item) {
        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(item)
                .booker(user)
                .bookingStatus(BookingStatus.WAITING)
                .build();
    }
}
