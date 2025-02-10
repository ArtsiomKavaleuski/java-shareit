package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(String name, String description);

    Collection<Item> findAllByOwnerId(Long ownerId);

}
