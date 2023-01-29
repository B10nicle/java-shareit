package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface ItemService {
    Item create(Item item, Long id);

    Item update(Item item, Long id);

    Item get(Long id);

    void delete(Long id);

    List<Item> getAll(Long id);

    List<Item> search(String text, Long userId);
}