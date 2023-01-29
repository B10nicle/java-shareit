package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Oleg Khilko
 */

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final Long ownerId;
}
