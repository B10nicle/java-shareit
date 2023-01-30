package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Oleg Khilko
 */

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final Long ownerId;
    private Long requestId;
}
