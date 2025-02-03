package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Booking addBooking(Long userId, BookingDtoRequest bookingDtoRequest) {
        userService.getUser(userId);
        Item item = itemRepository.findById(bookingDtoRequest.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));
        if (bookingDtoRequest.getStart().equals(bookingDtoRequest.getEnd())) {
            throw new BadRequestException("Start and end should not be equal");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available");
        }
        Booking booking = BookingMapper.toBooking(
                bookingDtoRequest,
                item,
                userService.getUser(userId));
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking approveBooking(Long userId, Long bookingId, String isApproved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new BadRequestException("Booking can only be approved by owner");
        }
        if (isApproved.equalsIgnoreCase("true")) {
            booking.setStatus(BookingStatus.APPROVED);
        } else if (isApproved.equalsIgnoreCase("false")) {
            booking.setStatus(BookingStatus.REJECTED);
        } else {
            throw new BadRequestException("Parameter APPROVED should be true or false");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Long userId, Long bookingId) {
        userService.getUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()  -> new NotFoundException("Booking not found"));
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new BadRequestException("Data is available only for owner or booker");
        }
        return booking;
    }

    @Override
    public List<Booking> getBookingListByBooker(Long userId, String state) {
        userService.getUser(userId);
        List<Booking> bookingList;
        switch (state) {
            case "ALL" -> bookingList = bookingRepository.findAllByBookerId(userId);
            case "CURRENT" -> bookingList = bookingRepository.findCurrentByBookerId(userId);
            case "PAST" -> bookingList = bookingRepository.findPastByBookerId(userId);
            case "FUTURE" -> bookingList = bookingRepository.findFutureByBookerId(userId);
            case "WAITING" -> bookingList = bookingRepository.findWaitingByBookerId(userId);
            case "REJECTED" -> bookingList = bookingRepository.findRejectedByBookerId(userId);
            default -> throw new BadRequestException("State is not valid");
        }
        return bookingList;
    }

    @Override
    public List<Booking> getBookingListByOwner(Long userId, String state) {
        userService.getUser(userId);
        List<Booking> bookingList;
        switch (state) {
            case "ALL" -> bookingList = bookingRepository.findAllByOwnerId(userId);
            case "CURRENT" -> bookingList = bookingRepository.findCurrentByOwnerId(userId);
            case "PAST" -> bookingList = bookingRepository.findPastByOwnerId(userId);
            case "FUTURE" -> bookingList = bookingRepository.findFutureByOwnerId(userId);
            case "WAITING" -> bookingList = bookingRepository.findWaitingByOwnerId(userId);
            case "REJECTED" -> bookingList = bookingRepository.findRejectedByOwnerId(userId);
            default -> throw new BadRequestException("State is not valid");
        }
        return bookingList;
    }
}
