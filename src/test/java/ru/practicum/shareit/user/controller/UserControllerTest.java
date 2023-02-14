package ru.practicum.shareit.user.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.error.NotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.error.EmailException;
import ru.practicum.shareit.user.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.http.MediaType.*;
import static java.nio.charset.StandardCharsets.*;
import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * @author Oleg Khilko
 */

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Michael")
            .email("michael@mail.com")
            .build();

    @Test
    void saveTest() throws Exception {
        when(userService.save(any(UserDto.class)))
                .thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void updateTest() throws Exception {
        when(userService.update(any(), anyLong()))
                .thenReturn(userDto);
        mvc.perform(patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.get(any()))
                .thenReturn(userDto);
        mvc.perform(get("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        mvc.perform(delete("/users/{userId}", 1)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void getAllTest() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(userDto));
        mvc.perform(get("/users")
                )
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }

    @Test
    void saveUserEmailExceptionTest() throws Exception {
        when(userService.save(any(UserDto.class)))
                .thenThrow(EmailException.class);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void saveUserValidationExceptionTest() throws Exception {
        when(userService.save(any(UserDto.class)))
                .thenThrow(ValidationException.class);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserNotFoundExceptionTest() throws Exception {
        when(userService.get(any()))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/users/{userId}", 7)
                )
                .andExpect(status().isNotFound());
    }
}
