package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private ItemRequestService itemRequestService;
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository);

        user = User.builder()
                .id(1L)
                .name("Test")
                .email("Test@test.com")
                .build();
        when(userRepository.save(any())).thenReturn(user);

        itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.now())
                .description("Test")
                .build();


        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        when(itemRequestRepository.findAllByRequesterId(anyLong())).thenReturn(List.of(itemRequest));
        when(itemRequestRepository.findByIdOrderByCreatedAsc(anyLong())).thenReturn(Optional.of(itemRequest));

        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Test
    void getAllItemRequestByOwner() {
        List<ItemRequestDto> result = itemRequestService.getAllItemRequestsByOwner(user.getId()).stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getCreated(), itemRequestDto.getCreated());
        Assertions.assertEquals(result.getFirst().getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(result.getFirst().getRequester(), itemRequestDto.getRequester());
        Assertions.assertEquals(result.getFirst().getId(), itemRequestDto.getId());
        verify(itemRequestRepository, times(1)).findAllByRequesterIdOrderByCreatedDesc(user.getId());
    }

    @Test
    void addItemRequest() {
        ItemRequestDto result = itemRequestService.addItemRequest(user.getId(), itemRequestDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(result.getRequester(), itemRequestDto.getRequester());
        Assertions.assertEquals(result.getId(), itemRequestDto.getId());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void getItemRequestById() {
        ItemRequestDto result = itemRequestService.getItemRequestById(itemRequest.getRequester().getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(result.getRequester(), itemRequestDto.getRequester());
        Assertions.assertEquals(result.getId(), itemRequestDto.getId());
        verify(itemRequestRepository, times(1)).findByIdOrderByCreatedAsc(anyLong());
    }

    @Test
    void getAllItemRequestsOfOtherUsers() {
        List<ItemRequestDto> result = itemRequestService.getAllItemRequestsOfOtherUsers(user.getId()).stream().toList();
        Assertions.assertTrue(result.isEmpty());
        verify(itemRequestRepository, times(1)).findAllWithoutRequester(user.getId());
    }
}
