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
    private final BookingService bookingService;
    private final ItemService itemService;

    @PostMapping()
    public BookingAllFieldsDto save(@RequestBody BookingSavingDto bookingSavingDto,
                                    @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                    Long userId) {
        var item = itemService.get(bookingSavingDto.getItemId(), userId);
        return bookingService.save(bookingSavingDto, item, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingAllFieldsDto approve(@PathVariable Long bookingId,
                                       @RequestParam(required = false) boolean approved,
                                       @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                       Long userId) {
        return bookingService.approve(bookingId, approved, userId);
    }

    @GetMapping("/owner")
    public List<BookingAllFieldsDto> getBookingsByOwner(@RequestParam(required = false) String state,
                                                        @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                                        Long userId) {
        return bookingService.getBookingsByOwner(userId, state);
    }

    @GetMapping()
    public List<BookingAllFieldsDto> getAll(@RequestParam(required = false) String state,
                                            @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                            Long userId) {
        return bookingService.getAll(userId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingAllFieldsDto get(@PathVariable Long bookingId,
                                   @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                   Long userId) {
        return bookingService.get(bookingId, userId);
    }
}
