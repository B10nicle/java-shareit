package ru.practicum.shareit.user.controller;

import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;
import java.util.List;

/**
 * @author Oleg Khilko
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto create(@RequestBody UserDto userDto) {
        var user = userService.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable Long userId) {
        var user = UserMapper.toUser(userDto);
        user.setId(userId);
        return UserMapper.toUserDto(userService.update(user));
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        return UserMapper.toUserDto(userService.get(userId));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @GetMapping()
    public List<UserDto> getAll() {
        return userService.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
