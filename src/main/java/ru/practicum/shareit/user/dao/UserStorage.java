package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.Optional;
import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface UserStorage {
    User create(User user);

    User update(User user);

    Optional<User> get(Long id);

    Optional<User> delete(Long id);

    List<User> getAll();
}
