package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.error.NotFoundException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static java.time.LocalDateTime.*;
import static org.mockito.Mockito.*;
import static java.util.List.*;

/**
 * @author Oleg Khilko
 */

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceUnitTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    ItemRequestService itemRequestService;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    private UserDto userDto;

    @BeforeEach
    void initialize() {
        userDto = new UserDto(
                1L,
                "marry",
                "marry@mail.com");
        itemRequestDto = new ItemRequestDto(
                1L,
                "my request",
                1L,
                now(),
                of()
        );
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemService);
        itemRequest = mapToItemRequest(itemRequestDto, userDto);
    }

    ItemRequestDto saveItemRequestDto() {
        when(userService.get(any()))
                .thenReturn(userDto);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        return itemRequestService.save(itemRequestDto, userDto.getId());
    }

    @Test
    void saveItemEmptyDescriptionTest() {
        var exception = assertThrows(ValidationException.class,
                () -> itemRequestService.save(
                        new ItemRequestDto(
                                1L,
                                "",
                                1L,
                                now(),
                                of()
                        ),
                        1L)
        );
        assertEquals("Request cannot be null or blank", exception.getMessage());
    }

    @Test
    void saveItemRequestTest() {
        var dto = saveItemRequestDto();
        assertEquals(dto.getRequesterId(), itemRequest.getRequester().getId());
        assertEquals(dto.getDescription(), itemRequest.getDescription());
        assertEquals(dto.getId(), itemRequest.getId());
    }

    @Test
    void getItemRequestsTest() {
        saveItemRequestDto();
        when(itemRequestRepository.findItemRequestByRequesterOrderByCreatedDesc(any()))
                .thenReturn(of(itemRequest));
        var allItemRequests = itemRequestService.getAllItemRequests(userDto.getId());
        assertEquals(allItemRequests.get(0).getId(), itemRequest.getId());
        assertEquals(allItemRequests.get(0).getItems().size(), 0);
        assertEquals(allItemRequests.size(), 1);
    }

    @Test
    void getItemRequestsItemsTest() {
        saveItemRequestDto();
        when(itemService.getItemsByRequests(any()))
                .thenReturn(of(
                                new ItemDto(
                                        1L,
                                        "Toy",
                                        "my toy",
                                        true,
                                        3L,
                                        1L)
                        )
                );
        when(itemRequestRepository.findItemRequestByRequesterOrderByCreatedDesc(any()))
                .thenReturn(of(itemRequest));
        var allItemRequests = itemRequestService.getAllItemRequests(userDto.getId());
        assertEquals(allItemRequests.get(0).getId(), itemRequest.getId());
        assertEquals(allItemRequests.get(0).getItems().size(), 1);
        assertEquals(allItemRequests.size(), 1);
    }

    @Test
    void getItemRequestTest() {
        saveItemRequestDto();
        when(itemService.getItemsByRequestId(any()))
                .thenReturn(of(
                                new ItemDto(
                                        1L,
                                        "toy",
                                        "my toy",
                                        true,
                                        3L,
                                        1L)
                        )
                );
        when(itemRequestRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(itemRequest));
        var itemRequestById = itemRequestService.getItemRequestById(2, userDto.getId());
        assertEquals(itemRequestById.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestById.getId(), itemRequest.getId());
        assertEquals(itemRequestById.getItems().size(), 1);
    }

    @Test
    void getItemRequestsEmptyTest() {
        saveItemRequestDto();
        when(itemRequestRepository.findItemRequestByRequesterOrderByCreatedDesc(any()))
                .thenReturn(of());
        var allItemRequests = itemRequestService.getAllItemRequests(userDto.getId());
        assertEquals(allItemRequests.size(), 0);
    }

    @Test
    void getAllItemRequestsTest() {
        saveItemRequestDto();
        when(itemService.getItemsByRequests(any()))
                .thenReturn(of(
                                new ItemDto(
                                        1L,
                                        "toy",
                                        "my toy",
                                        true,
                                        3L,
                                        1L)
                        )
                );
        when(itemRequestRepository.findItemRequestByRequester_IdIsNotOrderByCreatedDesc(any()))
                .thenReturn(of(itemRequest));
        var allItemRequests = itemRequestService.getAllItemRequests(null, null, userDto.getId());
        assertEquals(allItemRequests.get(0).getId(), itemRequest.getId());
        assertEquals(allItemRequests.get(0).getItems().size(), 1);
        assertEquals(allItemRequests.size(), 1);
    }

    @Test
    void getItemRequestNotFoundTest() {
        when(userService.get(any()))
                .thenReturn(userDto);
        when(itemRequestRepository.findById(any()))
                .thenThrow(NotFoundException.class);
        when(itemService.getItemsByRequestId(any()))
                .thenReturn(of());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(42L, 42L));
    }
}
