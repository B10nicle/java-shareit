package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface UserService {
    User create(User user);

    User update(User user);

    User get(Long id);

    void delete(Long id);

    List<User> getAll();
}
