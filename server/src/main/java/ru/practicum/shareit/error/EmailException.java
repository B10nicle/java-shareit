package ru.practicum.shareit.error;

/**
 * @author Oleg Khilko
 */

public class EmailException extends RuntimeException {
    public EmailException(String message) {
        super(message);
    }
}
