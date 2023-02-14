package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface ItemRequestService {
    List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Long userId);

    ItemRequestDto save(ItemRequestDto itemRequestDto, Long requesterId);

    ItemRequestDto getItemRequestById(long requestId, Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId);
}
