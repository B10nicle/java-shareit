package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.*;

/**
 * @author Oleg Khilko
 */

@Component
public class UserInMemoryStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> delete(Long id) {
        return Optional.ofNullable(users.remove(id));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
