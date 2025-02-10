package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDto> getItemsByOwner(Long ownerId);

    ItemDto getItemById(Long userId, Long itemId);

    ItemDto addItem(Long userId, ItemDto itemDto);

    Item updateItem(Long userId, Long itemId, Item item);

    Collection<Item> getItemsBySearch(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
