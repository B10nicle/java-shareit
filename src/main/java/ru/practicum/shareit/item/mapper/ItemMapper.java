package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item toItem(ItemAllFieldsDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static ItemAllFieldsDto toItemAllFieldsDto(Item item,
                                                      BookingAllFieldsDto lastBooking,
                                                      BookingAllFieldsDto nextBooking,
                                                      List<CommentDto> comments) {
        return ItemAllFieldsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(lastBooking != null ? new BookingDto(lastBooking.getId(), lastBooking.getBooker().getId()) : null)
                .nextBooking(nextBooking != null ? new BookingDto(nextBooking.getId(), nextBooking.getBooker().getId()) : null)
                .comments(comments != null ? comments : List.of())
                .build();
    }
}
