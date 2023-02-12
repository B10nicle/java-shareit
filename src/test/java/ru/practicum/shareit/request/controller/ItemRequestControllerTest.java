package ru.practicum.shareit.request.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.request.service.ItemRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.http.MediaType.*;
import static java.nio.charset.StandardCharsets.*;
import static org.mockito.ArgumentMatchers.*;
import static java.time.LocalDateTime.of;
import static java.time.Month.DECEMBER;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;

/**
 * @author Oleg Khilko
 */

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private final LocalDateTime time = of(2000, DECEMBER, 5, 0, 5, 10);
    private final String headerSharerUserId = "X-Sharer-User-Id";
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("I need this pen")
            .requesterId(1L)
            .created(time)
            .items(null)
            .build();

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.save(any(), anyLong()))
                .thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                )
                .andExpect(jsonPath("$.requesterId", is(itemRequestDto.getRequesterId().intValue())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.items", nullValue()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequests() throws Exception {
        when(itemRequestService.getAllItemRequests(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests/all")
                        .header(headerSharerUserId, 1)
                        .param("size", "1")
                        .param("from", "0")
                )
                .andExpect(jsonPath("$[0].requesterId", is(itemRequestDto.getRequesterId().intValue())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items", nullValue()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequestsById() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong()))
                .thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                        .header(headerSharerUserId, 1)
                )
                .andExpect(jsonPath("$[0].requesterId", is(itemRequestDto.getRequesterId().intValue())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].items", nullValue()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);
        mvc.perform(get("/requests/{requestId}", 1)
                        .header(headerSharerUserId, 1)
                )
                .andExpect(jsonPath("$.requesterId", is(itemRequestDto.getRequesterId().intValue())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.items", nullValue()))
                .andExpect(status().isOk());
    }
}
