package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.error.EmailException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;

import java.util.List;

import static ru.practicum.shareit.user.mapper.UserMapper.*;
import static java.util.stream.Collectors.*;

/**
 * @author Oleg Khilko
 */

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        validate(userDto);
        try {
            return toUserDto(userRepository.save(toUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new EmailException("User with email: " + userDto.getEmail() + " is already exist.");
            }
        }
        return null;
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long userId) {
        var user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User with ID #" + userId + " does not exist.");
        });
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());

        try {
            return toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new EmailException("User with email: " + userDto.getEmail() + " is already exist.");
            }
        }
        return null;
    }

    @Override
    public UserDto get(Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null.");
        var user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User with ID #" + userId + " does not exist.");
        });
        return toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null.");
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    private void validate(UserDto userDto) {
        if (userDto.getEmail() == null)
            throw new ValidationException("Email cannot be empty.");
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@"))
            throw new ValidationException("Incorrect email: " + userDto.getEmail() + ".");
    }
}