package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getAllItemRequestsByOwner(Long userId);

    Collection<ItemRequestDto> getAllItemRequestsOfOtherUsers(Long userId);

    ItemRequestDto getItemRequestById(Long requestId);

}