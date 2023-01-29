package ru.practicum.shareit.item.controller;

import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;
import java.util.List;

/**
 * @author Oleg Khilko
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDto(itemService.create(ItemMapper.toItem(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        var item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        return ItemMapper.toItemDto(itemService.update(item, userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        return ItemMapper.toItemDto(itemService.get(itemId));
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
    }

    @GetMapping()
    public List<ItemDto> getAll(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false) String text,
                                @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        return itemService.search(text, userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
