package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface BookingService {

    List<BookingAllFieldsDto> getBookingsByOwnerId(Long userId, String state, Integer from, Integer size);

    List<BookingAllFieldsDto> getAllBookings(Long bookerId, String state, Integer from, Integer size);

    BookingAllFieldsDto save(BookingSavingDto booking, ItemAllFieldsDto itemDto, Long bookerId);

    BookingAllFieldsDto approve(Long bookingId, boolean approved, Long userId);

    List<BookingAllFieldsDto> getBookingsByOwnerId(Long userId, String state);

    List<BookingAllFieldsDto> getBookingsByItem(Long itemId, Long userId);

    List<BookingAllFieldsDto> getAllBookings(Long bookerId, String state);

    BookingAllFieldsDto getBookingById(Long bookingId, Long userId);
}
