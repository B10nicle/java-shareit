package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.repository.BookingRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.enums.BookingTimeState;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.error.NotFoundException;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import static ru.practicum.shareit.booking.mapper.BookingMapper.*;
import static ru.practicum.shareit.booking.enums.BookingState.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.user.mapper.UserMapper.*;
import static java.util.stream.Collectors.*;
import static java.util.Arrays.*;

/**
 * @author Oleg Khilko
 */

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingAllFieldsDto save(BookingSavingDto bookingSavingDto, ItemAllFieldsDto itemDto, Long bookerId) {
        if (itemDto.getOwnerId().equals(bookerId))
            throw new NotFoundException("Item with id#" + itemDto.getId() + " cannot be booked by his owner");
        if (!itemDto.getAvailable())
            throw new ValidationException("Item with id#" + itemDto.getId() + " cannot be booked");
        validate(bookingSavingDto);
        var booker = toUser(userService.get(bookerId));
        var item = toItem(itemDto);
        var bookings = bookingRepository
                .findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(
                        item.getId(),
                        APPROVED,
                        bookingSavingDto.getStart()
                );
        if (!bookings.isEmpty())
            throw new NotFoundException("This item cannot be booked: " + item.getName());
        var booking = mapToBooking(bookingSavingDto);
        booking.setStatus(WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        var savedBooking = bookingRepository.save(booking);
        return mapToBookingAllFieldsDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingAllFieldsDto approve(Long bookingId, boolean approved, Long userId) {
        var booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with id#" + bookingId + " does not exist"));
        if (booking.getBooker().getId().equals(userId))
            throw new NotFoundException("There is no available approve for the user with id#" + userId);
        if (!booking.getItem().getOwner().getId().equals(userId)
                || !booking.getStatus().equals(WAITING))
            throw new ValidationException("Booking state cannot be updated");
        booking.setStatus(approved ? APPROVED : REJECTED);
        var savedBooking = bookingRepository.save(booking);
        return mapToBookingAllFieldsDto(savedBooking);
    }

    @Override
    public List<BookingAllFieldsDto> getBookingsByOwner(Long userId, String state) {
        var userDto = userService.get(userId);
        var user = toUser(userDto);
        if (state == null || BookingTimeState.ALL.name().equals(state)) {
            return bookingRepository.findBookingsByItemOwnerIsOrderByStartDesc(user)
                    .stream()
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        }
        if (BookingTimeState.PAST.name().equals(state)) {
            return bookingRepository.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        }
        if (BookingTimeState.CURRENT.name().equals(state)) {
            return bookingRepository.findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(
                            user, LocalDateTime.now(), LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        }
        if (BookingTimeState.FUTURE.name().equals(state)) {
            return bookingRepository.findBookingsByItemOwnerAndStartAfterOrderByStartDesc(
                            user, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        }
        if (stream(values()).anyMatch(bookingState -> bookingState.name().equals(state))) {
            return bookingRepository.findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(
                            user, valueOf(state))
                    .stream()
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        }
        throw new ValidationException("Unknown state: " + state);
    }

    @Override
    public List<BookingAllFieldsDto> getBookingsByItem(Long itemId, Long userId) {
        return bookingRepository.findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(
                        itemId, userId)
                .stream()
                .map(BookingMapper::mapToBookingAllFieldsDto)
                .collect(toList());
    }

    @Override
    public List<BookingAllFieldsDto> getAll(Long bookerId, String state) {
        var userDto = userService.get(bookerId);
        var booker = toUser(userDto);
        if (state == null || BookingTimeState.ALL.name().equals(state)) {
            return bookingRepository.findBookingsByBookerIsOrderByStartDesc(booker)
                    .stream()
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        }
        if (BookingTimeState.PAST.name().equals(state)) {
            return bookingRepository.findBookingsByBookerIsAndEndBeforeOrderByStartDesc(
                            booker, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        }
        if (BookingTimeState.CURRENT.name().equals(state)) {
            return bookingRepository.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(
                            booker, LocalDateTime.now(), LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        }
        if (BookingTimeState.FUTURE.name().equals(state)) {
            return bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(
                            booker, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        }
        if (stream(values()).anyMatch(bookingState -> bookingState.name().equals(state))) {
            return bookingRepository.findBookingsByBookerIsAndStatusIsOrderByStartDesc(
                            booker, valueOf(state))
                    .stream()
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        }
        throw new ValidationException("Unknown state: " + state);
    }

    @Override
    public BookingAllFieldsDto get(Long bookingId, Long userId) {
        var booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with id#" + bookingId + " does not exist"));
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("There is no available approve for the user with id#" + userId);
        }
        return mapToBookingAllFieldsDto(booking);
    }

    private void validate(BookingSavingDto bookingSavingDto) {
        if (bookingSavingDto.getStart() == null)
            throw new ValidationException("Please enter your start booking date");
        if (bookingSavingDto.getEnd() == null)
            throw new ValidationException("Please enter your end booking date");
        if (bookingSavingDto.getStart().toLocalDate().isBefore(LocalDate.now()))
            throw new ValidationException("Incorrect start booking date");
        if (bookingSavingDto.getEnd().isBefore(bookingSavingDto.getStart())
                || bookingSavingDto.getEnd().toLocalDate().isBefore(LocalDate.now()))
            throw new ValidationException("Incorrect end booking date");
    }
}
