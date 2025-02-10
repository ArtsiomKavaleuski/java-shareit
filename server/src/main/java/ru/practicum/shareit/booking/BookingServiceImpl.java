package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto addBooking(Long userId, BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart()) || bookingRequestDto.getEnd().isEqual(bookingRequestDto.getStart())) {
            throw new ValidationException("End date should be after start date");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found"));
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Item not found"));
        if (item.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Owner can not book his own item");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available");
        }
        Booking booking = BookingMapper.toBooking(bookingRequestDto,user,item);
        return BookingMapper.toBookingRequestDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        userRepository.findById(userId).orElseThrow(() ->
                new ValidationException("User not found"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking not found"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new BadRequestException("Only owner can approve booking");
        }
        if (approved) {
            booking.setBookingStatus(BookingStatus.APPROVED);
        } else {
            booking.setBookingStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingRequestDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking not found"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BadRequestException("Data is available only for owner or booker");
        }
        return BookingMapper.toBookingRequestDto(booking);
    }

    @Override
    public Collection<BookingDto> getBookingListByBooker(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found"));
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("State is not valid");
        }
        Collection<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };
        return BookingMapper.toListBookingRequestDto(bookings);
    }

    @Override
    public Collection<BookingDto> getBookingListByOwner(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found"));
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("State is not valid");
        }
        Collection<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };
        return BookingMapper.toListBookingRequestDto(bookings);
    }
}
