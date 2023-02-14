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
    public static Item mapToItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item mapToItem(ItemAllFieldsDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static ItemAllFieldsDto mapToItemAllFieldsDto(Item item,
                                                         BookingAllFieldsDto lastBooking,
                                                         BookingAllFieldsDto nextBooking,
                                                         List<CommentDto> comments) {
        return new ItemAllFieldsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking != null ? new BookingDto(lastBooking.getId(), lastBooking.getBooker().getId()) : null,
                nextBooking != null ? new BookingDto(nextBooking.getId(), nextBooking.getBooker().getId()) : null,
                comments != null ? comments : List.of()
        );
    }
}
