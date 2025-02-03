package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId, @Validated @RequestBody BookingDtoRequest bookingDtoRequest) {
        return BookingMapper.toBookingDto(bookingService.addBooking(userId, bookingDtoRequest));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId, @RequestParam(name = "approved") String isApproved) {
        return BookingMapper.toBookingDto(bookingService.approveBooking(userId, bookingId, isApproved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return BookingMapper.toBookingDto(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    public List<BookingDto> getBookingListByBooker(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return BookingMapper.toBookingDtoList(bookingService.getBookingListByBooker(userId, state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingListByOwner(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return BookingMapper.toBookingDtoList(bookingService.getBookingListByOwner(userId, state));
    }

}
