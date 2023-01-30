package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //for user
    List<Booking> findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User user,
                                                                                     LocalDateTime startDateTime,
                                                                                     LocalDateTime endDateTime);

    List<Booking> findBookingsByItemOwnerAndStartAfterOrderByStartDesc(User user,
                                                                       LocalDateTime localDateTime);

    List<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User user,
                                                                      LocalDateTime localDateTime);

    List<Booking> findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(User user,
                                                                       BookingState bookingState);

    List<Booking> findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(Long itemId, Long userId);

    List<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User user);

    //for booker
    List<Booking> findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(User booker,
                                                                                  LocalDateTime startDateTime,
                                                                                  LocalDateTime endDateTime);

    List<Booking> findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(User booker,
                                                                        LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerIsAndEndBeforeOrderByStartDesc(User booker,
                                                                     LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerIsAndStatusIsOrderByStartDesc(User booker,
                                                                    BookingState bookingState);

    List<Booking> findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(Long itemId,
                                                                  BookingState bookingState,
                                                                  LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerIsOrderByStartDesc(User booker);
}
