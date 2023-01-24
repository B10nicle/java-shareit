package ru.practicum.shareit.booking.enums;

/**
 * @author Oleg Khilko
 */

public enum BookingStatus {
    WAITING(0),
    APPROVED(1),
    REJECTED(2),
    CANCELED(3);

    private final int id;

    BookingStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
