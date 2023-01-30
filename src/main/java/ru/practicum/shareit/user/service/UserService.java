package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto, Long userId);

    UserDto get(Long userId);

    void delete(Long userId);

    List<UserDto> getAll();
}
