package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDtoComments;

import java.util.List;

public class ItemResponseDto {
    Long id;
    String name;
    Long requestorId;
    List<ItemDtoComments> items;
}
