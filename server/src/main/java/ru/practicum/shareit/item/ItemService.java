package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDtoOwner> getItemsByOwner(long ownerId);

    ItemDtoComments getItemById(long id);

    Item addItem(long userId, ItemDtoRequest itemDtoRequest);

    Item updateItem(long userId, long itemId, ItemDtoRequest itemDtoRequest);

    List<Item> getItemsBySearch(String text);

    Comment addComment(CommentDtoRequest commentDtoRequest, Long userId, Long itemId);

}
