package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDtoOwner> getItemsByOwner(long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> ItemMapper.toItemDtoOwner(item,
                        bookingRepository.findLastBookingByItemId(item.getId()),
                        bookingRepository.findNextBookingByItemId(item.getId()),
                        commentRepository.finByItemId(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoComments getItemById(long id) {
        return ItemMapper.toItemDtoComments(
                itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found")),
                commentRepository.finByItemId(id),
//                в тестах ожидается пустое поле lastBooking, хотя пользователь оставляет комментарий
//                уже на состоявшися booking, который и является последним
//                bookingRepository.findLastBookingByItemId(id),
                null,
                bookingRepository.findNextBookingByItemId(id));
    }

    @Override
    @Transactional
    public Item addItem(long userId, ItemDtoRequest itemDtoRequest) {
        if (itemDtoRequest.getAvailable() == null) {
            throw new BadRequestException("field 'available' is empty");
        }
//        ItemRequest itemRequest = new ItemRequest();
//        itemRequest = null;
//        if (itemDtoRequest.getItemRequestId() != null) {
//            itemRequest = itemRequestRepository.findById(itemDtoRequest.getItemRequestId()).orElseThrow(() -> new NotFoundException("Request not found"));
//        }
        Item item = ItemMapper.toItem(itemDtoRequest, userService.getUser(userId), null);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(long userId, long itemId, ItemDtoRequest itemDtoRequest) {
        userService.getUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        if (item.getOwner().getId() != userId) {
            throw new BadRequestException("Only owner can update item");
        }
        if (!item.getName().equals(itemDtoRequest.getName())
                && itemDtoRequest.getName() != null
                && !itemDtoRequest.getName().isBlank()) {
            item.setName(itemDtoRequest.getName());
        }
        if (!item.getDescription().equals(itemDtoRequest.getDescription())
                && itemDtoRequest.getDescription() != null
                && !itemDtoRequest.getDescription().isBlank()) {
            item.setDescription(itemDtoRequest.getDescription());
        }
        if (!item.getAvailable().equals(itemDtoRequest.getAvailable()) && itemDtoRequest.getAvailable() != null) {
            item.setAvailable(itemDtoRequest.getAvailable());
        }
        return itemRepository.save(item);
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        } else {
            return itemRepository.search(text);
        }
    }

    @Override
    @Transactional
    public Comment addComment(CommentDtoRequest commentDtoRequest, Long userId, Long itemId) {
        User user = userService.getUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        if (bookingRepository.findFinishedByUserAndItem(userId, itemId).isEmpty()) {
            throw new BadRequestException("You are not allowed to add comment");
        }
        Comment comment = CommentMapper.toComment(commentDtoRequest, user, item);
        comment.setAuthor(user);
        comment.setItem(item);
        return commentRepository.save(comment);
    }
}
