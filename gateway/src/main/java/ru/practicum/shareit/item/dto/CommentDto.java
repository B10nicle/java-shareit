package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

/**
 * @author Oleg Khilko
 */

@Data
public class CommentDto {
    private final Long id;
    @NotEmpty(message = "Text cannot be null")
    private final String text;
    private final Integer itemId;
    private final String authorName;
    private final LocalDateTime created;
}