package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("test@gmail.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("owner")
                .email("owner@gmail.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .description("description")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        itemDto = ItemMapper.toItemDto(item, null, null);

        comment = Comment.builder()
                .id(1L)
                .text("comment")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("commentDto")
                .authorName(owner.getName())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void addItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.addItem(user.getId(), itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void addItemWithUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.addItem(999L, itemDto);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.updateItem(owner.getId(), item.getId(), item);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItemWithUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(999L, item.getId(), item);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItemWithItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(user.getId(), 999L, item);
        });

        assertEquals("Item not found", exception.getMessage());
        verify(itemRepository, times(1)).findById(999L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItemNotOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            itemService.updateItem(user.getId(), item.getId(), item);
        });

        assertEquals("Only owner can update item", exception.getMessage());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItemById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto result = itemService.getItemById(user.getId(), item.getId());

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void getItemByIdUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(999L, item.getId());
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getItemBySearch() {
        when(itemRepository.findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(anyString(), anyString()))
                .thenReturn(Collections.singletonList(item));

        Collection<Item> result = itemService.getItemsBySearch("item");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(anyString(), anyString());
    }

    @Test
    void getItemBySearchWWithEmptyText() {
        Collection<Item> result = itemService.getItemsBySearch("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(anyString(), anyString());
    }

    @Test
    void getItemsByOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));

        Collection<ItemDto> result = itemService.getItemsByOwner(owner.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findAllByOwnerId(owner.getId());
    }

    @Test
    void addComment() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(Booking.builder().build()));
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CommentDto result = itemService.addComment(1L, 1L, commentDto);
        assertNotNull(result);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addItemShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addItem(user.getId(), itemDto));
    }

    @Test
    void updateItemShouldReturnUpdatedItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.updateItem(owner.getId(), item.getId(), item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), item.getId(), item));
    }

    @Test
    void updateItemShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), item.getId(), item));
    }

    @Test
    void getItemByIdShouldReturnItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto result = itemService.getItemById(user.getId(), item.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
    }

    @Test
    void getItemByIdShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(user.getId(), item.getId()));
    }

    @Test
    void getItemByIdShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(user.getId(), item.getId()));
    }

    @Test
    void getItemBySearchItemShouldReturnItems() {
        when(itemRepository.findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(anyString(), anyString()))
                .thenReturn(Collections.singletonList(item));

        Collection<Item> result = itemService.getItemsBySearch("item");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.iterator().next().getId());
    }

    @Test
    void getItemsBySearchShouldReturnEmptyCollectionWhenTextIsEmpty() {
        Collection<Item> result = itemService.getItemsBySearch("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getItemsByOwnerShouldReturnItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));

        Collection<ItemDto> result = itemService.getItemsByOwner(user.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.iterator().next().getId());
    }

    @Test
    void getItemsByOwnerShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemsByOwner(user.getId()));
    }

    @Test
    void addCommentShouldReturnComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mock(Booking.class)));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
    }

    @Test
    void addCommentShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void addCommentShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void addCommentShouldThrowValidationExceptionWhenNoBookingFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }
}
