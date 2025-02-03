package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoOwner {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long itemRequestId;
    private BookingDtoOwner lastBooking;
    private BookingDtoOwner nextBooking;
    private List<CommentDto> comments;
}
