package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long userId);

    Collection<ItemRequest> findAllByRequesterId(Long requesterId);

    Optional<ItemRequest> findByIdOrderByCreatedAsc(Long itemRequestId);

    @Query("select r from ItemRequest r where r.requester.id != ?1 order by r.created DESC")
    Collection<ItemRequest> findAllWithoutRequester(Long userId);
}
