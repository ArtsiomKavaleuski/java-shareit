package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {
    public static ItemDtoRequest toItemDto(Item item) {
        return new ItemDtoRequest(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest() != null ? item.getItemRequest().getId() : null
        );
    }

    public static Item toItem(ItemDtoRequest itemDtoRequest, User user, ItemRequest itemRequest) {
        Item item = new Item();
        item.setId(itemDtoRequest.getId());
        item.setName(itemDtoRequest.getName());
        item.setDescription(itemDtoRequest.getDescription());
        item.setAvailable(itemDtoRequest.getAvailable());
        if (user != null) item.setOwner(user);
        if (itemRequest != null) item.setItemRequest(itemRequest);
        return item;
    }

    public static ItemDtoOwner toItemDtoOwner(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        return new ItemDtoOwner(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getItemRequest() != null ? item.getItemRequest().getId() : null,
                lastBooking != null ? BookingMapper.toBookingDtoOwner(lastBooking) : null,
                nextBooking != null ? BookingMapper.toBookingDtoOwner(nextBooking) : null,
                comments.stream()
                        .map(CommentMapper::toDto)
                        .toList()
        );
    }

    public static ItemDtoComments toItemDtoComments(Item item, List<Comment> comments, Booking lastBooking, Booking nextBooking) {
        return new ItemDtoComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                comments.stream()
                        .map(CommentMapper::toDto)
                        .toList(),
                lastBooking != null ? BookingMapper.toBookingDtoOwner(lastBooking) : null,
                nextBooking != null ? BookingMapper.toBookingDtoOwner(nextBooking) : null,
                item.getItemRequest() != null ? item.getItemRequest().getId() : null
        );
    }

}
