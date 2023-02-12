package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.dto.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static ru.practicum.shareit.booking.enums.BookingState.WAITING;
import static ru.practicum.shareit.booking.enums.BookingState.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static java.time.LocalDateTime.now;
import static java.util.List.of;

/**
 * @author Oleg Khilko
 */

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private BookingAllFieldsDto bookingAllFieldsDto;
    private final BookingService bookingService;
    private final EntityManager entityManager;
    private final UserService userService;
    private final ItemService itemService;
    private ItemDto itemDto;
    private UserDto owner;

    @BeforeEach
    void initialize() {
        owner = userService.save(
                new UserDto(
                        null,
                        "Lora",
                        "lora@mail.com")
        );
        var booker = userService.save(
                new UserDto(
                        null,
                        "Mike",
                        "mike@mail.com")
        );
        itemDto = itemService.save(
                new ItemDto(
                        null,
                        "pen",
                        "blue",
                        true,
                        owner.getId(),
                        null),
                null,
                owner.getId()
        );
        var itemAllFieldsDto = new ItemAllFieldsDto(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                true,
                owner.getId(),
                null,
                null,
                null,
                of()
        );

        BookingSavingDto bookingSavingDto = BookingSavingDto.builder()
                .id(1L)
                .start(now())
                .end(now().plusHours(2))
                .itemId(1L)
                .booker(1L)
                .status(WAITING.name())
                .build();

        bookingAllFieldsDto = bookingService.save(
                bookingSavingDto,
                itemAllFieldsDto,
                booker.getId()
        );
    }

    @Test
    void saveTest() {
        var booking = entityManager
                .createQuery(
                        "SELECT booking " +
                                "FROM Booking booking",
                        Booking.class)
                .getSingleResult();
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBooker().getId(),
                equalTo(bookingAllFieldsDto.getBooker().getId()));
        assertThat(booking.getItem().getId(),
                equalTo(bookingAllFieldsDto.getItem().getId()));
    }

/*    @Test
    void approveTest() {
        var approved = bookingService.approve(
                bookingAllFieldsDto.getId(),
                true,
                bookingAllFieldsDto.getItem().getOwnerId()
        );
        var booking = entityManager
                .createQuery(
                        "SELECT booking " +
                                "FROM Booking booking",
                        Booking.class)
                .getSingleResult();
        assertThat(booking.getBooker().getId(),
                equalTo(approved.getBooker().getId()));
        assertThat(booking.getItem().getId(),
                equalTo(approved.getItem().getId()));
        assertThat(booking.getStatus().name(),
                equalTo(approved.getStatus()));
        assertThat(booking.getId(),
                equalTo(approved.getId()));
    }*/

    @Test
    void getAllBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                null,
                null,
                null);
        var booking = entityManager.createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "WHERE booking.booker.id = :id",
                        Booking.class)
                .setParameter("id", bookingAllFieldsDto.getBooker().getId())
                .getResultList();
        assertThat(approved.get(0).getId(),
                equalTo(booking.get(0).getId()));
        assertThat(approved.size(),
                equalTo(booking.size()));
    }

    @Test
    void getBookingsByOwnerIdStatusTest() {
        var bookings = bookingService.getBookingsByOwnerId(
                owner.getId(),
                APPROVED.name(),
                null,
                null);
        var approvedBookings = entityManager.createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "JOIN booking.item item " +
                                "WHERE item.owner.id = :id AND booking.status = :status",
                        Booking.class)
                .setParameter("id", owner.getId())
                .setParameter("status", APPROVED)
                .getResultList();
        assertThat(bookings.size(),
                equalTo(approvedBookings.size()));
        assertThat(bookings.size(),
                equalTo(0));
    }

    @Test
    void getBookingsByItemTest() {
        var bookingsFrom = bookingService.getBookingsByItem(
                itemDto.getId(),
                owner.getId()
        );
        var bookings = entityManager.createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "JOIN booking.item item " +
                                "WHERE item.owner.id = :ownerId AND item.id = :itemId",
                        Booking.class)
                .setParameter("itemId", itemDto.getId())
                .setParameter("ownerId", owner.getId())
                .getResultList();
        assertThat(bookingsFrom.get(0).getId(),
                equalTo(bookings.get(0).getId()));
        assertThat(bookingsFrom.size(),
                equalTo(bookings.size()));
    }

    @Test
    void getAllBookingsEmptyListTest() {
        var allBookings = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                APPROVED.name(),
                null,
                null);
        var approved = entityManager.createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "WHERE booking.booker.id = :id AND booking.status = :status",
                        Booking.class)
                .setParameter("id", bookingAllFieldsDto.getBooker().getId())
                .setParameter("status", APPROVED)
                .getResultList();
        assertThat(allBookings.size(),
                equalTo(approved.size()));
        assertThat(allBookings.size(),
                equalTo(0));
    }

    @Test
    void getBookingsByOwnerIdTest() {
        var bookings = bookingService.getBookingsByOwnerId(
                owner.getId(),
                null,
                null,
                null);
        var booking = entityManager.createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "JOIN booking.item item " +
                                "WHERE item.owner.id = :id",
                        Booking.class)
                .setParameter("id", owner.getId())
                .getResultList();
        assertThat(bookings.get(0).getId(),
                equalTo(booking.get(0).getId()));
        assertThat(bookings.size(),
                equalTo(booking.size()));
    }

    @Test
    void getBookingByIdTest() {
        var approved = bookingService.getBookingById(
                bookingAllFieldsDto.getId(),
                bookingAllFieldsDto.getBooker().getId()
        );
        var booking = entityManager
                .createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "WHERE booking.id = :id AND booking.booker.id = :bookerId",
                        Booking.class)
                .setParameter("bookerId", bookingAllFieldsDto.getBooker().getId())
                .setParameter("id", bookingAllFieldsDto.getId())
                .getSingleResult();
        assertThat(approved.getItem().getId(),
                equalTo(booking.getItem().getId()));
        assertThat(approved.getStart(),
                equalTo(booking.getStart()));
        assertThat(approved.getId(),
                equalTo(booking.getId()));
    }
}
