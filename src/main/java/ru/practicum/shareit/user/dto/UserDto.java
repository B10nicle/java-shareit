package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author Oleg Khilko
 */

@Data
@Builder
public class UserDto {
    private final Long id;
    private final String name;
    private final String email;
}
