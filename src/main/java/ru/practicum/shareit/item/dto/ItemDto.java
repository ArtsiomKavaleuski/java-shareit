package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long itemRequestId;
}
