package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface BookingService {

    BookingAllFieldsDto save(BookingSavingDto booking, ItemAllFieldsDto itemDto, Long bookerId);

    BookingAllFieldsDto approve(Long bookingId, boolean approved, Long userId);

    List<BookingAllFieldsDto> getBookingsByOwner(Long userId, String state);

    List<BookingAllFieldsDto> getBookingsByItem(Long itemId, Long userId);

    List<BookingAllFieldsDto> getAll(Long bookerId, String state);

    BookingAllFieldsDto get(Long bookingId, Long userId);
}
