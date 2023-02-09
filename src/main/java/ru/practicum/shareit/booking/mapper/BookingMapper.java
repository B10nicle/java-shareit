package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.booking.model.Booking;

/**
 * @author Oleg Khilko
 */

public class BookingMapper {
    public static Booking mapToBooking(BookingSavingDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public static BookingAllFieldsDto mapToBookingAllFieldsDto(Booking booking) {
        return BookingAllFieldsDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem() != null ? ItemMapper.mapToItemDto(booking.getItem()) : null)
                .booker(booking.getBooker() != null ? UserMapper.mapToUserDto(booking.getBooker()) : null)
                .status(booking.getStatus().name())
                .build();
    }
}
