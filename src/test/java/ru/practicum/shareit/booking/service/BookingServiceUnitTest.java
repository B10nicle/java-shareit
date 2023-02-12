package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.error.NotFoundException;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static ru.practicum.shareit.user.mapper.UserMapper.mapToUser;
import static ru.practicum.shareit.booking.enums.BookingTimeState.*;
import static ru.practicum.shareit.booking.enums.BookingState.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.lenient;
import static java.time.LocalDateTime.*;
import static org.mockito.Mockito.*;
import static java.util.List.*;

/**
 * @author Oleg Khilko
 */

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {
    @Mock
    private BookingRepository bookingRepository;
    private BookingSavingDto bookingCreatedDto;
    private BookingService bookingService;
    @Mock
    private UserService userService;
    private UserDto userDto;
    private Booking booking;

    @BeforeEach
    void initialize() {
        bookingService = new BookingServiceImpl(bookingRepository, userService);
        bookingCreatedDto = BookingSavingDto.builder()
                .id(1L)
                .start(now())
                .end(now().plusHours(2))
                .itemId(1L)
                .booker(2L)
                .status(WAITING.name())
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Lora")
                .email("lora@mail.com")
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(now())
                .end(now().plusHours(2))
                .item(new Item(1L, "pen", "blue pen", true, mapToUser(userDto), null))
                .booker(new User(2L, "Maggie", "maggie@mail.com"))
                .status(WAITING)
                .build();
    }

    private BookingAllFieldsDto saveBookingDto() {
        when(userService.get(any()))
                .thenReturn(userDto);
        when(bookingRepository.findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(anyLong(), any(), any()))
                .thenReturn(of());
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        return bookingService.save(
                bookingCreatedDto,
                mapToItemAllFieldsDto(
                        booking.getItem(),
                        null,
                        null,
                        of()
                ),
                2L);
    }

    @Test
    void saveBookingEmptyEndTimeTest() {
        lenient().when(userService.get(anyLong()))
                .thenReturn(userDto);
        bookingCreatedDto.setEnd(null);
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.save(
                        bookingCreatedDto,
                        mapToItemAllFieldsDto(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        2L)
        );
        assertEquals("Please enter your end booking date", exception.getMessage());
    }

    @Test
    void saveBookingTest() {
        var bookingAllFieldsDto = saveBookingDto();
        assertEquals(bookingAllFieldsDto.getId(), booking.getId());
        assertEquals(bookingAllFieldsDto.getItem().getId(), booking.getItem().getId());
    }

    @Test
    void saveBookingEmptyStartTimeTest() {
        lenient().when(userService.get(anyLong()))
                .thenReturn(userDto);
        bookingCreatedDto.setStart(null);
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.save(
                        bookingCreatedDto,
                        mapToItemAllFieldsDto(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        2L)
        );
        assertEquals("Please enter your start booking date", exception.getMessage());
    }

    @Test
    void saveBookingStartInPastTest() {
        lenient().when(userService.get(anyLong()))
                .thenReturn(userDto);
        bookingCreatedDto.setStart(now().minusDays(2));
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.save(
                        bookingCreatedDto,
                        mapToItemAllFieldsDto(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        2L)
        );
        assertEquals("Incorrect start booking date", exception.getMessage());
    }

    @Test
    void saveBookingEmptyEndInPastTest() {
        lenient().when(userService.get(anyLong()))
                .thenReturn(userDto);
        bookingCreatedDto.setEnd(now().minusDays(2));
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.save(
                        bookingCreatedDto,
                        mapToItemAllFieldsDto(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        2L)
        );
        assertEquals("Incorrect end booking date", exception.getMessage());
    }

    @Test
    void saveBookingByItemOwnerTest() {
        bookingCreatedDto.setBooker(booking.getItem().getOwner().getId());
        var exception = assertThrows(NotFoundException.class,
                () -> bookingService.save(
                        bookingCreatedDto,
                        mapToItemAllFieldsDto(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        bookingCreatedDto.getBooker())
        );
        assertEquals("Item with id#" + booking.getId() + " cannot be booked by his owner", exception.getMessage());
    }

    @Test
    void saveBookingNotAvailableItemTest() {
        booking.getItem().setAvailable(false);
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.save(
                        bookingCreatedDto,
                        mapToItemAllFieldsDto(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        2L)
        );
        assertEquals("Item with id#" + booking.getId() + " cannot be booked", exception.getMessage());
    }

    @Test
    void saveBookingTakenItemTest() {
        when(userService.get(anyLong()))
                .thenReturn(userDto);
        when(bookingRepository.findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(anyLong(), any(), any()))
                .thenReturn(of(booking));
        var exception = assertThrows(NotFoundException.class,
                () -> bookingService.save(
                        bookingCreatedDto,
                        mapToItemAllFieldsDto(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        2L)
        );
        assertEquals("This item cannot be booked: " + booking.getItem().getName(), exception.getMessage());
    }

    @Test
    void approveBookingNotItemOwnerTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(ofNullable(booking));
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.approve(
                        booking.getId(),
                        true,
                        3L)
        );
        assertEquals("Booking state cannot be updated", exception.getMessage());
    }

    @Test
    void approveBookingTest() {
        var approved = new Booking(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(ofNullable(booking));
        when(bookingRepository.save(any()))
                .thenReturn(approved);
        var approvedFrom = bookingService.approve(
                booking.getId(),
                true,
                userDto.getId()
        );
        assertEquals(approvedFrom.getStatus(), approved.getStatus().name());
        assertEquals(approvedFrom.getId(), approved.getId());
    }

    @Test
    void approveBookingByBookerTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(ofNullable(booking));
        var exception = assertThrows(NotFoundException.class,
                () -> bookingService.approve(
                        booking.getId(),
                        true,
                        booking.getBooker().getId())
        );
        assertEquals("There is no available approve for the user with id#" + booking.getBooker().getId(), exception.getMessage());
    }

    @Test
    void getNotFoundBookingTest() {
        when(bookingRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(
                        7L,
                        7L)
        );
    }

    @Test
    void approveApprovedBookingTest() {
        booking.setStatus(APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(ofNullable(booking));
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.approve(
                        booking.getId(),
                        true,
                        userDto.getId())
        );
        assertEquals("Booking state cannot be updated", exception.getMessage());
    }

    @Test
    void getBookingTest() {
        var bookingAllFieldsDto = saveBookingDto();
        when(bookingRepository.findById(anyLong()))
                .thenReturn(ofNullable(booking));
        var bookingFrom = bookingService.getBookingById(
                bookingAllFieldsDto.getId(),
                userDto.getId()
        );
        assertEquals(bookingFrom.getItem().getId(), booking.getItem().getId());
        assertEquals(bookingFrom.getId(), booking.getId());
    }

    @Test
    void getBookingByAnotherUserTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(ofNullable(booking));
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(
                        booking.getId(),
                        7L)
        );
    }

    @Test
    void getAllBookingsIncorrectEndPaginationTest() {
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookings(
                        userDto.getId(),
                        "Unknown",
                        0,
                        0)
        );
        assertEquals("size <= 0 || from < 0", exception.getMessage());
    }

    @Test
    void getAllBookingsTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByBookerIsOrderByStartDesc(any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getAllBookings(
                userDto.getId(),
                null,
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsIncorrectStartPaginationTest() {
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookings(
                        userDto.getId(),
                        "Unknown",
                        -1,
                        14)
        );
        assertEquals("size <= 0 || from < 0", exception.getMessage());
    }

    @Test
    void getAllBookingsAllStateTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByBookerIsOrderByStartDesc(any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getAllBookings(
                userDto.getId(),
                ALL.name(),
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsWithNotValidStateTest() {
        saveBookingDto();
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookings(
                        userDto.getId(),
                        "Unknown",
                        null,
                        null)
        );
        assertEquals("Unknown state: Unknown", exception.getMessage());
    }

    @Test
    void getAllBookingsFutureStateTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(any(), any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getAllBookings(
                userDto.getId(),
                FUTURE.name(),
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsPastStateTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByBookerIsAndEndBeforeOrderByStartDesc(any(), any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getAllBookings(
                userDto.getId(),
                PAST.name(),
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsCurrentStateTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getAllBookings(
                userDto.getId(),
                CURRENT.name(),
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsEmptyTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(any(), any()))
                .thenReturn(of());
        var bookings = bookingService.getAllBookings(
                userDto.getId(),
                FUTURE.name(),
                null,
                null
        );
        assertEquals(bookings.size(), 0);
    }

    @Test
    void getAllBookingsRejectStateTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByBookerIsAndStatusIsOrderByStartDesc(any(), any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getAllBookings(
                userDto.getId(),
                REJECTED.name(),
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsCancelStateEmptyTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByBookerIsAndStatusIsOrderByStartDesc(any(), any()))
                .thenReturn(of());
        var bookings = bookingService.getAllBookings(
                userDto.getId(),
                CANCELED.name(),
                null,
                null
        );
        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByOwnerIdPastStateTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(any(), any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getBookingsByOwnerId(
                userDto.getId(),
                PAST.name(),
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByOwnerIdTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByItemOwnerIsOrderByStartDesc(any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getBookingsByOwnerId(
                userDto.getId(),
                null,
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByOwnerIdAllStateTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByItemOwnerIsOrderByStartDesc(any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getBookingsByOwnerId(
                userDto.getId(),
                ALL.name(),
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByOwnerIdInvalidStateTest() {
        saveBookingDto();
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.getBookingsByOwnerId(
                        userDto.getId(),
                        "Unknown",
                        null,
                        null)
        );
        assertEquals("Unknown state: Unknown", exception.getMessage());
    }

    @Test
    void getBookingsByOwnerIdFutureStateTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByItemOwnerAndStartAfterOrderByStartDesc(any(), any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getBookingsByOwnerId(
                userDto.getId(),
                FUTURE.name(),
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByOwnerIdRejectStateTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(any(), any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getBookingsByOwnerId(
                userDto.getId(),
                REJECTED.name(),
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByOwnerIdCurrentStateTest() {
        saveBookingDto();
        when(bookingRepository.findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(of(booking));
        var bookings = bookingService.getBookingsByOwnerId(
                userDto.getId(),
                CURRENT.name(),
                null,
                null
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByItemEmptyTest() {
        when(bookingRepository.findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(anyLong(), anyLong()))
                .thenReturn(of());
        var bookings = bookingService.getBookingsByItem(
                1L,
                2L
        );
        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByItemTest() {
        when(bookingRepository.findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(anyLong(), anyLong()))
                .thenReturn(of(booking));
        var bookings = bookingService.getBookingsByItem(
                1L,
                2L
        );
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }
}
