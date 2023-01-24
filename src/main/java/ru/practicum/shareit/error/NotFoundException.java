package ru.practicum.shareit.error;

/**
 * @author Oleg Khilko
 */

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
