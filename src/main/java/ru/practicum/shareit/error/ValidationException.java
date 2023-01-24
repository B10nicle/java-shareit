package ru.practicum.shareit.error;

/**
 * @author Oleg Khilko
 */

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
