package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Collection<ItemDto> getItemsByOwner(long ownerId) {
        return itemRepository.getItemsByOwner(ownerId);
    }

    @Override
    public ItemDto getItemById(long id) {
        return itemRepository.getItemById(id);
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        userService.getUser(userId);
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("field 'available' is empty");
        }
        return itemRepository.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        userService.getUser(userId);
        if (itemRepository.getItemById(itemId).getOwnerId() != userId) {
            throw new BadRequestException("only owner can update item");
        }
        return itemRepository.updateItem(userId, itemId, itemDto);
    }

    @Override
    public Collection<ItemDto> getItemsBySearch(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        } else {
            return itemRepository.getItemsBySearch(text);
        }
    }
}
