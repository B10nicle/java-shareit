package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface ItemService {
    ItemDto save(ItemDto item, Long userId);

    ItemDto update(ItemDto item, Long userId);

    ItemAllFieldsDto get(Long id, Long userId);

    void delete(Long itemId);

    List<ItemAllFieldsDto> getAllItems(Long userId);

    List<ItemDto> search(String text, Long userId);

    CommentDto createComment(CommentDto comment, Long itemId, Long userId);

    List<CommentDto> getAllComments();
}
