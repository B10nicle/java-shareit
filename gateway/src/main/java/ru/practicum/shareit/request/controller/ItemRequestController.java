package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * @author Oleg Khilko
 */

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping()
    public ResponseEntity<Object> getItemRequests(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                     @RequestHeader(required = false, value = "X-Sharer-User-Id") long userId) {
        return itemRequestClient.getAllItemRequests(from, size, userId);
    }

    @Validated
    @PostMapping()
    public ResponseEntity<Object> createItemRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                                    @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable int requestId,
                                                 @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemRequestClient.getItemRequest(requestId, userId);
    }
}
