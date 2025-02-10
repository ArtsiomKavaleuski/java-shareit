package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder(toBuilder = true)
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
    private Collection<ItemDtoComments> items;
}
