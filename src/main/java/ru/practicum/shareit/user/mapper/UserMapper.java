package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;

/**
 * @author Oleg Khilko
 */

public class UserMapper {
    public static User toUser(UserDto user) {
        return new User(
                user.getName(),
                user.getEmail()
        );
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
