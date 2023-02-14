package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Oleg Khilko
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requesterId;
    private LocalDateTime created;
    private List<ItemDto> items;
}
