//package ru.practicum.shareit.item;
//
//import org.springframework.stereotype.Repository;
//import ru.practicum.shareit.exception.NotFoundException;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.mapper.ItemMapper;
//import ru.practicum.shareit.item.model.Item;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//@Repository
//public class ItemRepositoryImpl implements ItemRepository {
//    private long firstId = 0;
//    private Map<Long, Item> items = new HashMap<>();
//
//    @Override
//    public Collection<ItemDto> getItemsByOwner(long ownerId) {
//        return items.values().stream().filter(i -> i.getOwnerId() == ownerId).map(ItemMapper::toItemDto).toList();
//    }
//
//    @Override
//    public ItemDto getItemById(long id) {
//        if (!items.containsKey(id)) {
//            throw new NotFoundException("Item does not exist");
//        }
//        return ItemMapper.toItemDto(items.get(id));
//    }
//
//    @Override
//    public ItemDto addItem(long userId, ItemDto itemDto) {
//        Item item = ItemMapper.toItem(itemDto);
//        item.setId(generateId());
//        item.setOwnerId(userId);
//        items.computeIfAbsent(item.getId(), k -> item);
//        return ItemMapper.toItemDto(item);
//    }
//
//    @Override
//    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
//        Item item = items.get(itemId);
//        if (!item.getName().equals(itemDto.getName())
//                && itemDto.getName() != null
//                && !itemDto.getName().isBlank()) {
//            item.setName(itemDto.getName());
//        }
//        if (!item.getDescription().equals(itemDto.getDescription())
//                && itemDto.getDescription() != null
//                && !itemDto.getDescription().isBlank()) {
//            item.setDescription(itemDto.getDescription());
//        }
//        if (!item.getAvailable().equals(itemDto.getAvailable()) && itemDto.getAvailable() != null) {
//            item.setAvailable(itemDto.getAvailable());
//        }
//        items.put(itemId, item);
//        return ItemMapper.toItemDto(item);
//    }
//
//    @Override
//    public Collection<ItemDto> getItemsBySearch(String text) {
//        return items.values().stream()
//                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase())
//                        || i.getDescription().toLowerCase().contains(text.toLowerCase()))
//                .filter(i -> i.getAvailable().equals(true)).map(ItemMapper::toItemDto).toList();
//    }
//
//    private long generateId() {
//        return firstId++;
//    }
//}
