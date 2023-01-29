package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Optional;
import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Optional<Item> get(Long id);

    Optional<Item> delete(Long id);

    List<Item> getAll();
}
