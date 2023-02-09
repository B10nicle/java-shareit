package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface ItemRequestService {
    ItemRequestDto save(ItemRequestDto itemRequestDto, Long requesterId);

    List<ItemRequestDto> getAll(Integer from, Integer size, Long userId);

    ItemRequestDto getItemRequestById(long requestId, Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId);
}
