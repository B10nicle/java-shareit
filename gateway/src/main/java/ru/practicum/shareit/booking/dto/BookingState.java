package ru.practicum.shareit.booking.dto;

import java.util.Optional;

/**
 * @author Oleg Khilko
 */

public enum BookingState {
    REJECTED,
    WAITING,
    CURRENT,
    FUTURE,
    PAST,
    ALL;

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState))
                return Optional.of(state);
        }
        return Optional.empty();
    }
}
