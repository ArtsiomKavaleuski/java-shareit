package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long userId);

    Collection<ItemRequest> findAllByRequestorId(Long requestorId);

    Optional<ItemRequest> findByIdOrderByCreatedAsc(Long itemRequestId);
}
