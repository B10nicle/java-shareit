package ru.practicum.shareit.booking.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.practicum.shareit.booking.enums.BookingState.*;
import static org.springframework.http.MediaType.*;
import static java.nio.charset.StandardCharsets.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static java.time.LocalDateTime.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static java.time.Month.*;
import static java.util.List.*;

/**
 * @author Oleg Khilko
 */

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private final ItemDto itemDto = new ItemDto(1L, "Pen", "Blue pen", true, 1L, 1L);
    private final LocalDateTime startTime = of(2000, DECEMBER, 3, 0, 5, 10);
    private final LocalDateTime endTime = of(2000, DECEMBER, 5, 0, 5, 10);
    private final UserDto userDto = new UserDto(1L, "Lora", "lora@mail.com");
    private final String headerSharerUserId = "X-Sharer-User-Id";
    @MockBean
    BookingService bookingService;
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

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

    @Test
    void getAllBookingsTest() throws Exception {
        when(bookingService.getAllBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(of(bookingAllFieldsDto));
        mvc.perform(get("/bookings")
                        .header(headerSharerUserId, 1)
                        .param("state", "All")
                        .param("size", "1")
                        .param("from", "0")
                )
                .andExpect(jsonPath("$[0].start", is(bookingAllFieldsDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingAllFieldsDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].id", is(bookingAllFieldsDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingAllFieldsDto.getStatus())))
                .andExpect(jsonPath("$[0].booker", notNullValue()))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByOwnerIdTest() throws Exception {
        when(bookingService.getBookingsByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(of(bookingAllFieldsDto));
        mvc.perform(get("/bookings/owner")
                        .header(headerSharerUserId, 1)
                        .param("state", "All")
                        .param("size", "1")
                        .param("from", "0")
                )
                .andExpect(jsonPath("$[0].start", is(bookingAllFieldsDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingAllFieldsDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].id", is(bookingAllFieldsDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingAllFieldsDto.getStatus())))
                .andExpect(jsonPath("$[0].booker", notNullValue()))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }

    @Test
    void saveTest() throws Exception {
        when(bookingService.save(any(), any(), anyLong()))
                .thenReturn(bookingAllFieldsDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingSavingDto))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.start", is(bookingAllFieldsDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingAllFieldsDto.getEnd().toString())))
                .andExpect(jsonPath("$.id", is(bookingAllFieldsDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingAllFieldsDto.getStatus())))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void approveTest() throws Exception {
        when(bookingService.approve(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingAllFieldsDto);
        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .content(mapper.writeValueAsString(bookingAllFieldsDto))
                        .param("approved", "true")
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.start", is(bookingAllFieldsDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingAllFieldsDto.getEnd().toString())))
                .andExpect(jsonPath("$.id", is(bookingAllFieldsDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingAllFieldsDto.getStatus())))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingAllFieldsDto);
        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header(headerSharerUserId, 1))
                .andExpect(jsonPath("$.start", is(bookingAllFieldsDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingAllFieldsDto.getEnd().toString())))
                .andExpect(jsonPath("$.id", is(bookingAllFieldsDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingAllFieldsDto.getStatus())))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsValidationExceptionTest() throws Exception {
        when(bookingService.getAllBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(ValidationException.class);
        mvc.perform(get("/bookings")
                        .header(headerSharerUserId, 1)
                        .param("state", "All")
                        .param("size", "1")
                        .param("from", "0")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByOwnerIdValidationExceptionTest() throws Exception {
        when(bookingService.getBookingsByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(ValidationException.class);
        mvc.perform(get("/bookings/owner")
                        .header(headerSharerUserId, 1)
                        .param("state", "All")
                        .param("size", "1")
                        .param("from", "0")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveNotFoundExceptionTest() throws Exception {
        when(bookingService.save(any(), any(), anyLong()))
                .thenThrow(NotFoundException.class);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingSavingDto))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void saveValidationExceptionTest() throws Exception {
        when(bookingService.save(any(), any(), anyLong()))
                .thenThrow(ValidationException.class);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingSavingDto))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveValidationExceptionTest() throws Exception {
        when(bookingService.approve(anyLong(), anyBoolean(), anyLong()))
                .thenThrow(ValidationException.class);
        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .content(mapper.writeValueAsString(bookingAllFieldsDto))
                        .param("approved", "true")
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingByIdNotFoundExceptionTest() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header(headerSharerUserId, 1)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void approveNotFoundExceptionTest() throws Exception {
        when(bookingService.approve(anyLong(), anyBoolean(), anyLong()))
                .thenThrow(NotFoundException.class);
        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .content(mapper.writeValueAsString(bookingAllFieldsDto))
                        .param("approved", "true")
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBookingsInternalServerErrorTest() throws Exception {
        when(bookingService.getAllBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(ValidationException.class);
        mvc.perform(get("/bookings")
                        .header(headerSharerUserId, 1)
                        .param("state", "All")
                        .param("size", "10000000000")
                        .param("from", "0")
                )
                .andExpect(status().is5xxServerError());
    }
}
