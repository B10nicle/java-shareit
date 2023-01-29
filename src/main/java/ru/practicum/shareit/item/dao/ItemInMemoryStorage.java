package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

/**
 * @author Oleg Khilko
 */

@Component
public class ItemInMemoryStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Optional<Item> delete(Long id) {
        return Optional.ofNullable(items.remove(id));
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }
}
