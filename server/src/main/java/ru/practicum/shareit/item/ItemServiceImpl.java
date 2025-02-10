package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Collection<ItemDto> getItemsByOwner(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found"));
        return ItemMapper.toListItemDto(itemRepository.findAllByOwnerId(userId));
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item not found"));
        return ItemMapper.toItemDto(item,
                BookingMapper.toBookingItemDto(
                        bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(
                                itemId,
                                userId,
                                LocalDateTime.now(),
                                BookingStatus.APPROVED)
                                .orElse(null)),
                BookingMapper.toBookingItemDto(
                        bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
                                itemId,
                                userId,
                                LocalDateTime.now(),
                                BookingStatus.APPROVED)
                                .orElse(null)));
    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found"));
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Item request not found"));

        }
        return ItemMapper.toItemDto(itemRepository.save(
                Item.builder()
                        .name(itemDto.getName())
                        .owner(user)
                        .description(itemDto.getDescription())
                        .available(itemDto.getAvailable())
                        .request(itemRequest)
                        .build()
        ), null, null);
    }

    @Override
    @Transactional
    public Item updateItem(Long userId, Long itemId, Item newItem) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found"));
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item not found"));
        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Only owner can update item");
        }
        newItem.setName(Optional.ofNullable(newItem.getName()).orElse(oldItem.getName()));
        newItem.setDescription(Optional.ofNullable(newItem.getDescription()).orElse(oldItem.getDescription()));
        newItem.setId(Optional.ofNullable(newItem.getId()).orElse(itemId));
        newItem.setAvailable(Optional.ofNullable(newItem.getAvailable()).orElse(oldItem.getAvailable()));
        newItem.setOwner(oldItem.getOwner());
        return itemRepository.save(newItem);
    }

    @Override
    public Collection<Item> getItemsBySearch(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return itemRepository.findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(text, text)
                    .stream()
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item not found"));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, BookingStatus.APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("You are not allowed to add comment");
        }
        Comment comment = CommentMapper.toComment(commentDto, author, item, LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}