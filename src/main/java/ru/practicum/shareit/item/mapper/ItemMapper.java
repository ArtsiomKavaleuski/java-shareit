package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest() != null ? item.getItemRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        if(user != null) item.setOwner(user);
        if(itemRequest != null) item.setItemRequest(itemRequest);
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
