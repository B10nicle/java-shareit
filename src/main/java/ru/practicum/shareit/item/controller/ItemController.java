package ru.practicum.shareit.item.controller;

import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.service.ItemService;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author Oleg Khilko
 */

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public ItemDto save(@RequestBody ItemDto itemDto,
                        @RequestHeader(value = "X-Sharer-User-Id", required = false)
                        Long userId) {
        return itemService.save(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(value = "X-Sharer-User-Id", required = false)
                          Long userId) {
        itemDto.setId(itemId);
        return itemService.update(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemAllFieldsDto get(@PathVariable Long itemId,
                                @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                Long userId) {
        return itemService.get(itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
    }

    @GetMapping()
    public List<ItemAllFieldsDto> getAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = false)
                                              Long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(value = "X-Sharer-User-Id", required = false)
                                @RequestParam(required = false) String text,
                                Long userId) {
        return itemService.search(text, userId);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto saveComment(@RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                    Long userId) {
        return itemService.saveComment(commentDto, itemId, userId);
    }
}
