package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    List<Item> getItemsByOwner(long ownerId);

    Item getItemById(long id);

    Item addItem(long userId, ItemDto itemDto);

    Item updateItem(long userId, long itemId, ItemDto itemDto);

    List<Item> getItemsBySearch(String text);

}
