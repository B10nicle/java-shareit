package ru.practicum.shareit.booking.controller;

import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.item.service.ItemService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author Oleg Khilko
 */

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private static final String HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;
    private final ItemService itemService;

    @PostMapping()
    public BookingAllFieldsDto save(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                                    @RequestBody BookingSavingDto bookingSavingDto) {
        var item = itemService.get(bookingSavingDto.getItemId(), userId);
        return bookingService.save(bookingSavingDto, item, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingAllFieldsDto approve(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                                       @RequestParam(required = false) boolean approved,
                                       @PathVariable Long bookingId) {
        return bookingService.approve(bookingId, approved, userId);
    }

    @GetMapping("/owner")
    public List<BookingAllFieldsDto> getBookingsByOwner(@RequestHeader(required = false, value = HEADER_SHARER_USER_ID) Long userId,
                                                        @RequestParam(required = false) String state,
                                                        @RequestParam(required = false) Integer from,
                                                        @RequestParam(required = false) Integer size) {
        return bookingService.getBookingsByOwnerId(userId, state, from, size);
    }

    @GetMapping()
    public List<BookingAllFieldsDto> getBookings(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                                                 @RequestParam(required = false) String state,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        return bookingService.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingAllFieldsDto get(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                                   @PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }
}
