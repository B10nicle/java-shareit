package ru.practicum.shareit.booking.dto;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.enums.BookingState.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static java.time.LocalDateTime.of;
import static java.time.Month.DECEMBER;

/**
 * @author Oleg Khilko
 */

@JsonTest
class BookingDtoTest {
    private final ItemDto itemDto = new ItemDto(1L, "Pen", "Blue pen", true, 1L, 1L);
    private final LocalDateTime startTime = of(2000, DECEMBER, 3, 0, 5, 10);
    private final LocalDateTime endTime = of(2000, DECEMBER, 5, 0, 5, 10);
    private final UserDto userDto = new UserDto(1L, "Lora", "lora@mail.com");
    @Autowired
    private JacksonTester<BookingAllFieldsDto> bookingAllFieldsDtoJacksonTester;
    @Autowired
    private JacksonTester<BookingSavingDto> bookingSavingDtoJacksonTester;
    @Autowired
    private JacksonTester<BookingDto> bookingDtoJacksonTester;

    private final BookingAllFieldsDto bookingAllFieldsDto = BookingAllFieldsDto.builder()
            .id(1L)
            .start(startTime)
            .end(endTime)
            .item(itemDto)
            .booker(userDto)
            .status(WAITING.name())
            .build();

    private final BookingSavingDto bookingSavingDto = BookingSavingDto.builder()
            .id(1L)
            .start(startTime)
            .end(endTime)
            .itemId(1L)
            .booker(1L)
            .status(WAITING.name())
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .bookerId(1L)
            .build();

    @Test
    void bookingDtoJacksonTesterTest() throws Exception {
        var jsonContent = bookingDtoJacksonTester.write(bookingDto);
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDto.getId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingDto.getBookerId().intValue());
    }

    @Test
    void bookingSavingDtoJacksonTesterTest() throws Exception {
        var jsonContent = bookingSavingDtoJacksonTester.write(bookingSavingDto);
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingSavingDto.getId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingSavingDto.getStart().toString());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingSavingDto.getEnd().toString());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingSavingDto.getItemId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.booker")
                .isEqualTo(bookingSavingDto.getBooker().intValue());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingSavingDto.getStatus());
    }

    @Test
    void bookingAllFieldsDtoJacksonTesterTest() throws Exception {
        var jsonContent = bookingAllFieldsDtoJacksonTester.write(bookingAllFieldsDto);
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingAllFieldsDto.getId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingAllFieldsDto.getStart().toString());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingAllFieldsDto.getEnd().toString());
        assertThat(jsonContent)
                .extractingJsonPathMapValue("$.item").isNotNull();
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(bookingAllFieldsDto.getItem().getId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingAllFieldsDto.getItem().getName());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.item.description")
                .isEqualTo(bookingAllFieldsDto.getItem().getDescription());
        assertThat(jsonContent)
                .extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(bookingAllFieldsDto.getItem().getAvailable());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.item.ownerId")
                .isEqualTo(bookingAllFieldsDto.getItem().getOwnerId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.item.requestId")
                .isEqualTo(bookingAllFieldsDto.getItem().getRequestId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathMapValue("$.booker")
                .isNotNull();
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookingAllFieldsDto.getBooker().getId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(bookingAllFieldsDto.getBooker().getName());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.booker.email")
                .isEqualTo(bookingAllFieldsDto.getBooker().getEmail());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingAllFieldsDto.getStatus());
    }
}
