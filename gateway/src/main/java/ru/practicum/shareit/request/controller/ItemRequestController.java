package ru.practicum.shareit.request.controller;

import ru.practicum.shareit.request.client.ItemRequestClient;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.AllArgsConstructor;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Positive;
import javax.validation.Valid;

/**
 * @author Oleg Khilko
 */

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @RequestHeader(required = false, value = HEADER_SHARER_USER_ID) Long userId,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getAllItemRequests(from, size, userId);
    }

    @Validated
    @PostMapping()
    public ResponseEntity<Object> createItemRequest(@RequestHeader(value = HEADER_SHARER_USER_ID) Long userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemRequests(@RequestHeader(value = HEADER_SHARER_USER_ID) Long userId) {
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(value = HEADER_SHARER_USER_ID) Long userId,
                                                 @PathVariable Long requestId) {
        return itemRequestClient.getItemRequest(requestId, userId);
    }
}
