package ru.practicum.shareit.item.model;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Oleg Khilko
 */

@Data
@AllArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean isAvailable;
    private User owner;
    private ItemRequest request;

    public Item(String name, String description, Boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
    }
}