package ru.aston.homework.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.json.JsonMapper;

import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.exception.EmailAlreadyTakenException;
import ru.aston.homework.exception.UserNotFoundException;
import ru.aston.homework.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String BASE_URL = "/api/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void create_validRequest_returnsCreated() throws Exception {
        var request = new UserCreateRequest(
                "Ivan", "ivan@example.com", 25
        );
        var response = userResponse(
                1L, "Ivan", "ivan@example.com", 25
        );

        when(userService.create(any())).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));

        verify(userService).create(any(UserCreateRequest.class));
    }

    @Test
    void create_invalidEmail_returns400() throws Exception {
        var request = new UserCreateRequest(
                "Ivan", "not-email", 25
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Ошибка валидации"))
                .andExpect(jsonPath("$.detail").exists());

        verifyNoInteractions(userService);
    }

    @Test
    void create_negativeAge_returns400() throws Exception {
        var request = new UserCreateRequest(
                "Ivan", "ivan@example.com", -1
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Ошибка валидации"))
                .andExpect(jsonPath("$.detail").exists());

        verifyNoInteractions(userService);
    }

    @Test
    void create_multipleValidationErrors_returnsProblemDetail() throws Exception {
        var request = new UserCreateRequest(
                "", "not-email", -1
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Ошибка валидации"))
                .andExpect(jsonPath("$.detail").exists());

        verifyNoInteractions(userService);
    }

    @Test
    void create_malformedJson_returns400() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Ошибка формата запроса"))
                .andExpect(jsonPath("$.detail")
                        .value("Некорректный JSON или тип данных в запросе"));

        verifyNoInteractions(userService);
    }

    @Test
    void create_emailTaken_returns409() throws Exception {
        var request = new UserCreateRequest(
                "Ivan", "ivan@example.com", 25
        );

        when(userService.create(any()))
                .thenThrow(new EmailAlreadyTakenException("ivan@example.com"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title")
                        .value("Электронная почта уже занята"))
                .andExpect(jsonPath("$.detail").exists());

        verify(userService).create(any(UserCreateRequest.class));
    }

    @Test
    void findById_found_returns200() throws Exception {
        when(userService.findById(1L))
                .thenReturn(Optional.of(
                        userResponse(1L, "Ivan", "ivan@example.com", 25)
                ));

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));

        verify(userService).findById(1L);
    }

    @Test
    void findById_notFound_returns404() throws Exception {
        when(userService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Пользователь не найден"))
                .andExpect(jsonPath("$.detail").exists());

        verify(userService).findById(99L);
    }

    @Test
    void findById_invalidIdFormat_returns400() throws Exception {
        mockMvc.perform(get(BASE_URL + "/not-a-number"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void findAll_returnsList() throws Exception {
        when(userService.findAll()).thenReturn(List.of(
                userResponse(1L, "A", "a@b.com", 20),
                userResponse(2L, "B", "b@b.com", 30)
        ));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[1].name").value("B"));

        verify(userService).findAll();
    }

    @Test
    void findAll_empty_returnsEmptyArray() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService).findAll();
    }

    @Test
    void update_validRequest_returns200() throws Exception {
        var request = new UserUpdateRequest(
                1L, "Ivan Updated", "ivan@example.com", 30
        );
        var response = userResponse(
                1L, "Ivan Updated", "ivan@example.com", 30
        );

        when(userService.update(any(UserUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan Updated"))
                .andExpect(jsonPath("$.age").value(30));

        verify(userService).update(any(UserUpdateRequest.class));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        var request = new UserUpdateRequest(
                99L, "Ivan", "ivan@example.com", 25
        );

        when(userService.update(any(UserUpdateRequest.class)))
                .thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(put(BASE_URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Пользователь не найден"))
                .andExpect(jsonPath("$.detail").exists());

        verify(userService).update(any(UserUpdateRequest.class));
    }

    @Test
    void update_emailTaken_returns409() throws Exception {
        var request = new UserUpdateRequest(
                1L, "Ivan", "taken@example.com", 25
        );

        when(userService.update(any(UserUpdateRequest.class)))
                .thenThrow(new EmailAlreadyTakenException("taken@example.com"));

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title")
                        .value("Электронная почта уже занята"))
                .andExpect(jsonPath("$.detail").exists());

        verify(userService).update(any(UserUpdateRequest.class));
    }

    @Test
    void update_invalidBody_returns400() throws Exception {
        var request = new UserUpdateRequest(
                -1L, "", "not-email", -1
        );

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Ошибка валидации"))
                .andExpect(jsonPath("$.detail").exists());

        verifyNoInteractions(userService);
    }

    @Test
    void update_idMismatch_returns400() throws Exception {
        var request = new UserUpdateRequest(
                2L, "Ivan", "ivan@example.com", 25
        );

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Несовпадение ID"))
                .andExpect(jsonPath("$.detail").exists());

        verifyNoInteractions(userService);
    }

    @Test
    void delete_existing_returns204() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new UserNotFoundException(99L))
                .when(userService).delete(99L);

        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Пользователь не найден"))
                .andExpect(jsonPath("$.detail").exists());

        verify(userService).delete(99L);
    }

    private static UserResponse userResponse(
            Long id,
            String name,
            String email,
            int age
    ) {
        return new UserResponse(
                id,
                name,
                email,
                age,
                LocalDateTime.of(2026, 1, 1, 12, 0)
        );
    }
}
