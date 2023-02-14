package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.error.NotFoundException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.practicum.shareit.error.EmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.user.mapper.UserMapper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static java.util.Optional.*;

/**
 * @author Oleg Khilko
 */

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;
    private UserService userService;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void initialize() {
        userService = new UserServiceImpl(userRepository);
        userDto = UserDto.builder()
                .id(1L)
                .name("Paul")
                .email("paul@mail.com")
                .build();
        user = mapToUser(userDto);
    }

    @Test
    void saveUserNullEmailTest() {
        var exception = assertThrows(ValidationException.class,
                () -> userService.save(new UserDto(
                        null,
                        "Molly",
                        null))
        );
        assertEquals("Email cannot be empty.", exception.getMessage());
    }

    @Test
    void saveTest() {
        when(userRepository.save(any()))
                .thenReturn(user);
        var save = userService.save(userDto);
        assertEquals(save.getEmail(), user.getEmail());
        assertEquals(save.getName(), user.getName());
        assertEquals(save.getId(), user.getId());
    }

    @Test
    void saveUserSameEmailTest() {
        when(userRepository.save(any()))
                .thenThrow(EmailException.class);
        assertThrows(EmailException.class,
                () -> userService.save(userDto));

    }

    @Test
    void saveUserEmptyEmailTest() {
        var exception = assertThrows(ValidationException.class,
                () -> userService.save(new UserDto(
                        null, "Abbie", "mail"))
        );
        assertEquals("Incorrect email: mail.", exception.getMessage());
    }

    @Test
    void updateUserNameTest() {
        var userDto1 = new UserDto(1L, "Daniel", null);
        var userDto2 = new UserDto(1L, "Daniel", userDto.getEmail());
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.save(userDto);
        when(userRepository.save(any()))
                .thenReturn(mapToUser(userDto2));
        when(userRepository.findById(any()))
                .thenReturn(ofNullable(mapToUser(userDto2)));
        var dto = userService.update(userDto1, userDto2.getId());
        assertNotEquals(dto.getEmail(), userDto1.getEmail());
        assertEquals(dto.getName(), userDto2.getName());
        assertEquals(dto.getId(), userDto2.getId());
    }

    @Test
    void updateTest() {
        var updatedUser = new UserDto(
                1L,
                "Nagel",
                "nagel@mail.com");
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.save(userDto);
        when(userRepository.save(any()))
                .thenReturn(mapToUser(updatedUser));
        when(userRepository.findById(any()))
                .thenReturn(ofNullable(mapToUser(updatedUser)));
        var dto = userService.update(updatedUser, updatedUser.getId());
        assertEquals(dto.getEmail(), updatedUser.getEmail());
        assertEquals(dto.getName(), updatedUser.getName());
        assertEquals(dto.getId(), updatedUser.getId());
    }

    @Test
    void getUserUserNotFoundTest() {
        when(userRepository.findById(any()))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class,
                () -> userService.get(7L));
    }

    @Test
    void updateUserEmailTest() {
        var userDto1 = new UserDto(1L, null, "john@mail.com");
        var userDto2 = new UserDto(1L, userDto.getName(), "john@mail.com");
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.save(userDto);
        when(userRepository.save(any()))
                .thenReturn(mapToUser(userDto2));
        when(userRepository.findById(any()))
                .thenReturn(ofNullable(mapToUser(userDto2)));
        var dto = userService.update(userDto1, userDto2.getId());
        assertNotEquals(dto.getName(), userDto1.getName());
        assertEquals(dto.getEmail(), userDto2.getEmail());
        assertEquals(dto.getId(), userDto2.getId());
    }

    @Test
    void updateUserSameEmailTest() {
        var dto = new UserDto(2L, "Paul", "paul@mail.com");
        when(userRepository.findById(any()))
                .thenReturn(ofNullable(mapToUser(dto)));
        when(userRepository.save(any()))
                .thenThrow(EmailException.class);
        assertThrows(EmailException.class,
                () -> userService.update(
                        userDto,
                        1L)
        );
    }

    @Test
    void deleteTest() {
        when(userRepository.save(any()))
                .thenReturn(user);
        var dto = userService.save(userDto);
        userService.delete(dto.getId());
        verify(userRepository, times(1))
                .deleteById(user.getId());
    }

    @Test
    void getUserNullTest() {
        var exception = assertThrows(ValidationException.class,
                () -> userService.get(null));
        assertEquals("User ID cannot be null.", exception.getMessage());
    }

    @Test
    void deleteUserNullIdTest() {
        var exception = assertThrows(ValidationException.class,
                () -> userService.delete(null));
        assertEquals("User ID cannot be null.", exception.getMessage());
    }

    @Test
    void getAllEmptyTest() {
        when(userRepository.findAll())
                .thenReturn(List.of());
        var dtos = userService.getAll();
        assertEquals(dtos.size(), 0);
    }

    @Test
    void getAllTest() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));
        var dtos = userService.getAll();
        assertEquals(dtos.get(0).getId(), user.getId());
        assertEquals(dtos.size(), 1);
    }
}
