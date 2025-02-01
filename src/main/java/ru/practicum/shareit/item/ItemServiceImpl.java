package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    //private final ItemRequestService itemRequestService;

    @Override
    public List<Item> getItemsByOwner(long ownerId) {
        return itemRepository.findByOwnerId(ownerId);
    }

    @Override
    public Item getItemById(long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public Item addItem(long userId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("field 'available' is empty");
        }
        Item item = ItemMapper.toItem(itemDto, userService.getUser(userId), null);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(long userId, long itemId, ItemDto itemDto) {
        userService.getUser(userId);
        if (itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found")).getOwner().getId() != userId) {
            throw new BadRequestException("Only owner can update item");
        }
        Item item = getItemById(itemId);
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
}
