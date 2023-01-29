package ru.practicum.shareit.item.service;

import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;

/**
 * @author Oleg Khilko
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private long id = 0;

    @Override
    public Item create(Item item, Long id) {
        validate(item);
        item.setOwner(userService.get(id));
        item.setId(generateId());
        return itemStorage.create(item);
    }

    @Override
    public Item update(Item item, Long id) {
        if (id == null) throw new ValidationException("User ID cannot be null.");
        var itemToUpdate = get(item.getId());
        if (!itemToUpdate.getOwner().getId().equals(id))
            throw new NotFoundException("Item with ID #" + id + " has another owner.");
        if (item.getName() != null) itemToUpdate.setName(item.getName());
        if (item.getDescription() != null) itemToUpdate.setDescription(item.getDescription());
        if (item.getIsAvailable() != null) itemToUpdate.setIsAvailable(item.getIsAvailable());
        return itemStorage.update(itemToUpdate);
    }

    @Override
    public Item get(Long id) {
        return itemStorage.get(id).orElseThrow(() -> {
            throw new NotFoundException("Item with ID #" + id + " does not exist.");
        });
    }

    @Override
    public void delete(Long id) {
        itemStorage.delete(id).orElseThrow(() -> {
            throw new NotFoundException("Item with ID #" + id + " does not exist.");
        });
    }

    @Override
    public List<Item> getAll(Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null.");
        return itemStorage.getAll()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text, Long userId) {
        if (text.isBlank()) return Collections.emptyList();
        return itemStorage.getAll()
                .stream()
                .filter(Item::getIsAvailable)
                .filter(item -> item.getDescription() != null
                        && item.getDescription().toLowerCase().contains(text.toLowerCase())
                        || item.getName() != null
                        && item.getName().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private long generateId() {
        return ++id;
    }

    private void validate(Item item) {
        if (item.getName() == null || item.getName().isBlank())
            throw new ValidationException("Name cannot be blank.");
        if (item.getDescription() == null || item.getDescription().isBlank())
            throw new ValidationException("Description cannot be blank.");
        if (item.getIsAvailable() == null)
            throw new ValidationException("Is Available cannot be null.");
    }
}
