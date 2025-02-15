package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingRequestDto bookingRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.addBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam(name = "approved") Boolean isApproved) {
        return bookingService.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getBookingListByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(name = "state", required = false,
                                                                 defaultValue = "ALL") String state) {
        return bookingService.getBookingListByBooker(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingListByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "state", required = false,
                                                                defaultValue = "ALL") String state) {
        return bookingService.getBookingListByOwner(userId, state);
    }

}
