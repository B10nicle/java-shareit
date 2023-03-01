package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.validation.Created;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * @author Oleg Khilko
 */

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping()
    public ResponseEntity<Object> getAllItems(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                              @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Field userId is null");
        }
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@NotNull @RequestParam(required = false) String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                              @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Field userId is null");
        }
        return itemClient.searchItems(text, userId, from, size);
    }

    @Validated(Created.class)
    @PostMapping()
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemDto itemDto,
                                             @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Field userId is null");
        }
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @PathVariable long itemId,
                                             @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Field userId is null");
        }
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        return itemClient.getItem(itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        itemClient.deleteItem(itemId);
    }

    @Validated
    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createItemComment(@RequestBody @Valid CommentDto commentDto,
                                                    @PathVariable long itemId,
                                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Field userId is null");
        }
        return itemClient.createComment(commentDto, itemId, userId);
    }
}
