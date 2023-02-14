package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //for owner
    List<Booking> findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User owner,
                                                                                     LocalDateTime startDateTime,
                                                                                     LocalDateTime endDateTime);

    Page<Booking> findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User owner,
                                                                                     LocalDateTime startDateTime,
                                                                                     LocalDateTime endDateTime,
                                                                                     Pageable pageable);

    List<Booking> findBookingsByItemOwnerAndStartAfterOrderByStartDesc(User owner,
                                                                       LocalDateTime localDateTime);

    Page<Booking> findBookingsByItemOwnerAndStartAfterOrderByStartDesc(User owner,
                                                                       LocalDateTime localDateTime,
                                                                       Pageable pageable);

    List<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User owner,
                                                                      LocalDateTime localDateTime);

    Page<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User owner,
                                                                      LocalDateTime localDateTime,
                                                                      Pageable pageable);

    List<Booking> findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(User owner,
                                                                       BookingState bookingState);

    Page<Booking> findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(User owner,
                                                                       BookingState bookingState,
                                                                       Pageable pageable);

    List<Booking> findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(Long itemId,
                                                                      Long userId);

    Page<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner,
                                                            Pageable pageable);

    List<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner);

    //for booker
    List<Booking> findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(User booker,
                                                                                  LocalDateTime startDateTime,
                                                                                  LocalDateTime endDateTime);

    Page<Booking> findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(User booker,
                                                                                  LocalDateTime startDateTime,
                                                                                  LocalDateTime endDateTime,
                                                                                  Pageable pageable);

    List<Booking> findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(User booker,
                                                                        LocalDateTime localDateTime);

    Page<Booking> findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(User booker,
                                                                        LocalDateTime localDateTime,
                                                                        Pageable pageable);

    List<Booking> findBookingsByBookerIsAndEndBeforeOrderByStartDesc(User booker,
                                                                     LocalDateTime localDateTime);

    Page<Booking> findBookingsByBookerIsAndEndBeforeOrderByStartDesc(User booker,
                                                                     LocalDateTime localDateTime,
                                                                     Pageable pageable);

    List<Booking> findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(Long itemId,
                                                                  BookingState bookingState,
                                                                  LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerIsAndStatusIsOrderByStartDesc(User booker,
                                                                    BookingState bookingState);

    Page<Booking> findBookingsByBookerIsAndStatusIsOrderByStartDesc(User booker,
                                                                    BookingState bookingState,
                                                                    Pageable pageable);

    Page<Booking> findBookingsByBookerIsOrderByStartDesc(User booker,
                                                         Pageable pageable);

    List<Booking> findBookingsByBookerIsOrderByStartDesc(User booker);
}
