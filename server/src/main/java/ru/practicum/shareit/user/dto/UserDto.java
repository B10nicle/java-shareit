package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Oleg Khilko
 */

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private final Long id;
    private final String name;
    private final String email;
}
