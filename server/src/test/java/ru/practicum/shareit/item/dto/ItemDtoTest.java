package ru.practicum.shareit.item.dto;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static java.time.LocalDateTime.*;
import static java.util.List.*;

/**
 * @author Oleg Khilko
 */

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemAllFieldsDto> itemAllFieldsDtoJacksonTester;
    @Autowired
    private JacksonTester<CommentDto> commentDtoJacksonTester;
    @Autowired
    private JacksonTester<ItemDto> itemDtoJacksonTester;

    @Test
    void itemDtoTest() throws Exception {
        var itemDto = ItemDto.builder()
                .id(1L)
                .name("Pen")
                .description("Blue pen")
                .available(true)
                .ownerId(1L)
                .requestId(1L)
                .build();

        var jsonContent = itemDtoJacksonTester.write(itemDto);
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(jsonContent)
                .extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto.getRequestId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.ownerId")
                .isEqualTo(itemDto.getOwnerId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDto.getId().intValue());
    }

    @Test
    void itemAllFieldsDtoTest() throws Exception {
        var itemAllFieldsDto = new ItemAllFieldsDto(
                1L,
                "Pen",
                "Blue pen",
                true,
                1L,
                null,
                new BookingDto(1L, 1L),
                null,
                of()
        );
        var jsonContent = itemAllFieldsDtoJacksonTester.write(itemAllFieldsDto);
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(itemAllFieldsDto.getLastBooking().getBookerId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(itemAllFieldsDto.getLastBooking().getId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo(itemAllFieldsDto.getDescription());
        assertThat(jsonContent)
                .extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemAllFieldsDto.getAvailable());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.ownerId")
                .isEqualTo(itemAllFieldsDto.getOwnerId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo(itemAllFieldsDto.getName());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemAllFieldsDto.getId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathArrayValue("$.comments")
                .isNullOrEmpty();
        assertThat(jsonContent)
                .extractingJsonPathMapValue("$.lastBooking")
                .isNotNull();
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.requestId")
                .isNull();
        assertThat(jsonContent)
                .extractingJsonPathValue("$.nextBooking")
                .isNull();
    }

    @Test
    void commentDtoTest() throws Exception {
        var commentDto = CommentDto.builder()
                .id(1L)
                .text("My comment")
                .itemId(1L)
                .authorName("Norris")
                .created(now())
                .build();
        var jsonContent = commentDtoJacksonTester.write(commentDto);
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(commentDto.getItemId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(commentDto.getId().intValue());
    }
}
