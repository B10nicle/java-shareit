package ru.practicum.shareit.utils;

import ru.practicum.shareit.error.ValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.springframework.data.domain.PageRequest.*;

/**
 * @author Oleg Khilko
 */

public class Pagination {
    public static PageRequest makePageRequest(Integer from, Integer size, Sort sort) {
        if (size == null || from == null) return null;
        if (size <= 0 || from < 0) throw new ValidationException("size <= 0 || from < 0");
        return of(from / size, size, sort);
    }
}
