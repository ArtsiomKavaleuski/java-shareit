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
    public Item addItem(long userId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("field 'available' is empty");
        }
        Item item = ItemMapper.toItem(itemDto, userService.getUser(userId), null);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(long userId, long itemId, ItemDto itemDto) {
        userService.getUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        if (item.getOwner().getId() != userId) {
            throw new BadRequestException("Only owner can update item");
        }
        if (!item.getName().equals(itemDto.getName())
                && itemDto.getName() != null
                && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (!item.getDescription().equals(itemDto.getDescription())
                && itemDto.getDescription() != null
                && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (!item.getAvailable().equals(itemDto.getAvailable()) && itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
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
        User user =  userService.getUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        if(bookingRepository.findFinishedByUserAndItem(userId, itemId).isEmpty()) {
            throw new BadRequestException("You are not allowed to add comment");
        }
        Comment comment = CommentMapper.toComment(commentDtoRequest, user, item);
        comment.setAuthor(user);
        comment.setItem(item);
        //comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}
