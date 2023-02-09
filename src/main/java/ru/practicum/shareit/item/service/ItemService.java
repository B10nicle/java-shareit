package ru.practicum.shareit.item.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface ItemService {
    List<ItemAllFieldsDto> getAllItems(Long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Long userId, Integer from, Integer size);

    ItemDto save(ItemDto itemDto, ItemRequestDto itemRequestDto, Long userId);

    CommentDto saveComment(CommentDto commentDto, Long itemId, Long userId);

    List<ItemDto> getItemsByRequests(List<ItemRequest> requests);

    List<ItemDto> getItemsByRequestId(Long requestId);

    List<CommentDto> getAllComments(Long itemId);

    ItemDto update(ItemDto itemDto, Long userId);

    ItemAllFieldsDto get(Long id, Long userId);

    List<CommentDto> getAllComments();

    void delete(Long itemId);
}
