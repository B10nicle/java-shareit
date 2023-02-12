package ru.practicum.shareit.item.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.request.service.ItemRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.service.ItemService;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.http.MediaType.*;
import static java.nio.charset.StandardCharsets.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static java.time.LocalDateTime.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static java.util.List.*;

/**
 * @author Oleg Khilko
 */

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private final String headerSharerUserId = "X-Sharer-User-Id";
    @MockBean
    ItemRequestService itemRequestService;
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("qwerty")
            .itemId(1L)
            .authorName("Paul")
            .created(now())
            .build();

    private final ItemAllFieldsDto itemExtendedDto = new ItemAllFieldsDto(
            1L,
            "blue pen",
            "my blue pen",
            true,
            1L,
            null,
            null,
            null,
            of(commentDto));

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("pen")
            .description("blue pen")
            .available(true)
            .ownerId(1L)
            .requestId(1L)
            .build();

    @Test
    void save() throws Exception {
        when(itemService.save(any(), any(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItems() throws Exception {
        when(itemService.getAllItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(of(itemExtendedDto));
        mvc.perform(get("/items")
                        .header(headerSharerUserId, 1)
                        .param("size", "1")
                        .param("from", "0")
                )
                .andExpect(jsonPath("$[0].description", is(itemExtendedDto.getDescription())))
                .andExpect(jsonPath("$[0].id", is(itemExtendedDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemExtendedDto.getName())))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }

    @Test
    void getItem() throws Exception {
        when(itemService.get(any(), anyLong()))
                .thenReturn(itemExtendedDto);
        mvc.perform(get("/items/{itemId}", 1)
                        .header(headerSharerUserId, 1)
                )
                .andExpect(jsonPath("$.description", is(itemExtendedDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemExtendedDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemExtendedDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        when(itemService.update(any(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                )
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItem() throws Exception {
        mvc.perform(delete("/items/{itemId}", 1)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void saveComment() throws Exception {
        when(itemService.saveComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(status().isOk());
    }

    @Test
    void search() throws Exception {
        when(itemService.search(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(of(itemDto));
        mvc.perform(get("/items/search")
                        .header(headerSharerUserId, 1)
                        .param("size", "1")
                        .param("from", "0")
                        .param("text", "")
                )
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }
}
