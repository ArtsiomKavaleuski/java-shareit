package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@example.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("User")
                .email("user@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();

        bookingDto = BookingMapper.toBookingRequestDto(booking);
    }

    @Test
    void addBookingSuccess() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingRequestDto.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.addBooking(user.getId(), bookingRequestDto);
        assertNotNull(result);
        assertEquals(result.getStart(), bookingDto.getStart());
        assertEquals(result.getEnd(), bookingDto.getEnd());
        assertEquals(result.getItem(), bookingDto.getItem());
        assertEquals(result.getStatus(), bookingDto.getStatus());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void addBookingStartEndValidationException() {
        BookingRequestDto invalidDto = BookingRequestDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now())
                .itemId(1L)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.addBooking(user.getId(), invalidDto));
        assertEquals("End date should be after start date", exception.getMessage());
    }

    @Test
    void addBookingUserNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.addBooking(user.getId(), bookingRequestDto));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void addBookingItemNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingRequestDto.getItemId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.addBooking(user.getId(), bookingRequestDto));
        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void addBookingItemNotAvailableException() {
        Item unavailableItem = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(false)
                .owner(owner)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingRequestDto.getItemId())).thenReturn(Optional.of(unavailableItem));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.addBooking(user.getId(), bookingRequestDto));
        assertEquals("Item is not available", exception.getMessage());
    }

    @Test
    void approveBookingSuccess() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.approveBooking(owner.getId(), booking.getId(), true);
        assertNotNull(result);
        assertEquals(result.getStart(), bookingDto.getStart());
        assertEquals(result.getEnd(), bookingDto.getEnd());
        assertEquals(result.getItem(), bookingDto.getItem());
        assertEquals(result.getStatus(), BookingStatus.APPROVED);
        assertEquals(result.getBooker(), bookingDto.getBooker());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void approveBookingBookingNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.approveBooking(user.getId(), booking.getId(), true));
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void approveBookingUserNotFoundException() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.approveBooking(user.getId(), booking.getId(), true));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getBookingSuccess() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        BookingDto result = bookingService.getBooking(user.getId(), booking.getId());

        assertEquals(booking.getId(), result.getId());
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    void getBookingNotFoundException() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getBooking(user.getId(), booking.getId()));
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void getBookingListByBookerSuccess() {
        String state = "ALL";
        Collection<Booking> bookings = Collections.singletonList(booking);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId())).thenReturn(bookings);

        Collection<BookingDto> result = bookingService.getBookingListByBooker(user.getId(), state);

        assertNotNull(result);
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(user.getId());
    }

    @Test
    void getBookingListByOwnerSuccess() {
        String state = "ALL";
        Collection<Booking> bookings = Collections.singletonList(booking);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(user.getId())).thenReturn(bookings);

        Collection<BookingDto> result = bookingService.getBookingListByOwner(user.getId(), state);

        assertNotNull(result);
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(user.getId());
    }

    @Test
    void addBookingWhenUserIsOwnerShouldThrowException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(bookingRequestDto.getItemId())).thenReturn(Optional.of(item));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.addBooking(owner.getId(), bookingRequestDto)
        );
        assertEquals("Owner can not book his own item", exception.getMessage());
    }

    @Test
    void getBookingListByBookerAllState() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId())).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByBooker(user.getId(), "ALL");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(user.getId());
    }

    @Test
    void getBookingListByBookerWaitingState() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING)).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByBooker(user.getId(), "WAITING");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING);
    }

    @Test
    void getBookingListByBookerRejectedState() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.REJECTED)).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByBooker(user.getId(), "REJECTED");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.REJECTED);
    }

    @Test
    void getBookingListByBookerCurrentState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        LocalDateTime now = LocalDateTime.now().withNano(0);
        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(owner.getId(), now, now)).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByBooker(owner.getId(), "CURRENT");

        assertEquals(0, result.size());
    }

    @Test
    void getBookingListByBookerFutureState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        LocalDateTime now = LocalDateTime.now().withNano(0);
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(owner.getId(), now)).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByBooker(owner.getId(), "FUTURE");

        assertEquals(0, result.size());
    }

    @Test
    void getBookingListByBookerPastState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        LocalDateTime now = LocalDateTime.now().withNano(0);
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(owner.getId(), now)).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByBooker(owner.getId(), "PAST");

        assertEquals(0, result.size());
    }



    @Test
    void getBookingListByBookerInvalidState() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.getBookingListByBooker(user.getId(), "INVALID"));

        assertEquals("State is not valid", exception.getMessage());
    }

    @Test
    void getBookingListByBookerNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getBookingListByBooker(user.getId(), "ALL"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getBookingListByOwnerAllState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId())).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByOwner(owner.getId(), "ALL");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(owner.getId());
    }

    @Test
    void getBookingListByOwnerWaitingState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), BookingStatus.WAITING)).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByOwner(owner.getId(), "WAITING");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), BookingStatus.WAITING);
    }

    @Test
    void getBookingListByOwnerRejectedState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), BookingStatus.REJECTED)).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByOwner(owner.getId(), "REJECTED");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), BookingStatus.REJECTED);
    }

    @Test
    void getBookingListByOwnerInvalidState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.getBookingListByOwner(owner.getId(), "INVALID"));

        assertEquals("State is not valid", exception.getMessage());
    }

    @Test
    void getBookingListByOwnerCurrentState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        LocalDateTime now = LocalDateTime.now().withNano(0);
        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(owner.getId(), now, now)).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByOwner(owner.getId(), "CURRENT");

        assertEquals(0, result.size());
    }

    @Test
    void getBookingListByOwnerFutureState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        LocalDateTime now = LocalDateTime.now().withNano(0);
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(owner.getId(), now)).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByOwner(owner.getId(), "FUTURE");

        assertEquals(0, result.size());
    }

    @Test
    void getBookingListByOwnerPastState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        LocalDateTime now = LocalDateTime.now().withNano(0);
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(owner.getId(), now)).thenReturn(Collections.singletonList(booking));

        Collection<BookingDto> result = bookingService.getBookingListByOwner(owner.getId(), "PAST");

        assertEquals(0, result.size());
    }

    @Test
    void getBookingListByOwnerNotFoundException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getBookingListByOwner(owner.getId(), "ALL"));

        assertEquals("User not found", exception.getMessage());
    }
}
