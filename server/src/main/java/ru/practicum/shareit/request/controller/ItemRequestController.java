package ru.practicum.shareit.request.controller;

import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author Oleg Khilko
 */

@RestController
@AllArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private static final String HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @GetMapping()
    public List<ItemRequestDto> getItemRequests(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId) {
        return itemRequestService.getAllItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        return itemRequestService.getAllItemRequests(from, size, userId);
    }

    @PostMapping()
    public ItemRequestDto createItemRequest(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.save(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                                         @PathVariable long requestId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
