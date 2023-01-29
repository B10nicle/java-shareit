package ru.practicum.shareit.user.service;

import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.EmailException;
import ru.practicum.shareit.user.dao.UserStorage;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Oleg Khilko
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private long id = 0;

    @Override
    public User create(User user) {
        validate(user);
        if (getAll().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new EmailException("User with ID #" + user.getEmail() + " is already exist.");
        }
        user.setId(generateId());
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        if (getAll().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new EmailException("User with ID #" + user.getEmail() + " is already exist.");
        }
        var userToUpdate = get(user.getId());
        if (user.getName() != null) userToUpdate.setName(user.getName());
        if (user.getEmail() != null) userToUpdate.setEmail(user.getEmail());
        return userStorage.update(userToUpdate);
    }

    @Override
    public User get(Long id) {
        if (id == null) throw new ValidationException("User ID cannot be null.");
        return userStorage.get(id).orElseThrow(() -> {
            throw new NotFoundException("User with ID #" + id + " does not exist.");
        });
    }

    @Override
    public void delete(Long id) {
        if (id == null) throw new ValidationException("User ID cannot be null.");
        userStorage.delete(id).orElseThrow(() -> {
            throw new NotFoundException("User with ID #" + id + " does not exist.");
        });
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    private long generateId() {
        return ++id;
    }

    private void validate(User user) {
        if (user.getEmail() == null)
            throw new ValidationException("Email cannot be empty.");
        if (user.getEmail().isBlank() || !user.getEmail().contains("@"))
            throw new ValidationException("Incorrect email: " + user.getEmail() + ".");
    }
}