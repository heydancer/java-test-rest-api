package com.heydancer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heydancer.common.dto.FullUserDTO;
import com.heydancer.common.dto.ShortUserDTO;
import com.heydancer.exception.NotFoundException;
import com.heydancer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class UserControllerTest {
    private static final long FAKE_ID = 99999L;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService service;
    private ShortUserDTO firstShortUserDTO;
    private FullUserDTO firstFullUserDTO;
    private ShortUserDTO secondShortUserDTO;

    @BeforeEach
    void beforeEach() {
        firstShortUserDTO = ShortUserDTO.builder()
                .firstname("firstname1")
                .lastname("lastname1")
                .surname("surname1")
                .birthday(LocalDate.of(1990, 2, 13))
                .build();

        firstFullUserDTO = FullUserDTO.builder()
                .id(1L)
                .firstname("firstname1")
                .lastname("lastname1")
                .surname("surname1")
                .birthday(LocalDate.of(1990, 2, 13))
                .build();

        secondShortUserDTO = ShortUserDTO.builder()
                .firstname("firstname2")
                .lastname("lastname2")
                .surname("surname2")
                .birthday(LocalDate.of(1980, 3, 15))
                .build();
    }

    @Test
    void shouldCreateAndReturnUser() throws Exception {
        String userDTOJson = objectMapper.writeValueAsString(firstFullUserDTO);

        when(service.add(any(ShortUserDTO.class)))
                .thenReturn(firstFullUserDTO);

        mvc.perform(post("/users")
                        .with(csrf())
                        .content(userDTOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(userDTOJson));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        when(service.getAll())
                .thenReturn(List.of(firstShortUserDTO, secondShortUserDTO));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].firstname", containsInAnyOrder("firstname1", "firstname2")))
                .andExpect(jsonPath("$[*].lastname", containsInAnyOrder("lastname1", "lastname2")))
                .andExpect(jsonPath("$[*].surname", containsInAnyOrder("surname1", "surname2")))
                .andExpect(jsonPath("$[*].birthday", containsInAnyOrder("1990-02-13", "1980-03-15")));
    }

    @Test
    void shouldReturnEmptyUsers() throws Exception {
        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        String userDTOJson = objectMapper.writeValueAsString(firstFullUserDTO);

        when(service.getById(1L))
                .thenReturn(firstFullUserDTO);

        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(userDTOJson));
    }

    @Test
    void shouldReturnAndCheckFakeId() throws Exception {
        String userDTOJson = objectMapper.writeValueAsString(firstFullUserDTO);

        when(service.getById(FAKE_ID))
                .thenThrow(new NotFoundException("User not found"));

        mvc.perform(get("/users/" + FAKE_ID)
                        .content(userDTOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldUpdateAndReturnUser() throws Exception {
        String userDTOJson = objectMapper.writeValueAsString(firstFullUserDTO);

        when(service.update(anyLong(), any(ShortUserDTO.class)))
                .thenReturn(firstFullUserDTO);

        mvc.perform(put("/users/1")
                        .with(csrf())
                        .content(userDTOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(userDTOJson));
    }

    @Test
    void shouldUpdateAndCheckEmptyName() throws Exception {
        firstShortUserDTO.setFirstname("");
        String userDTOJson = objectMapper.writeValueAsString(firstShortUserDTO);

        when(service.update(1L, firstShortUserDTO))
                .thenThrow(new RuntimeException());

        mvc.perform(put("/users/1")
                        .with(csrf())
                        .content(userDTOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldUpdateAndCheckEmptyFirstnameAndLastname() throws Exception {
        firstShortUserDTO.setFirstname("");
        firstShortUserDTO.setLastname("");
        String userDTOJson = objectMapper.writeValueAsString(firstShortUserDTO);

        when(service.update(1L, firstShortUserDTO))
                .thenThrow(new RuntimeException());

        mvc.perform(put("/users/1")
                        .with(csrf())
                        .content(userDTOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteUserById() throws Exception {
        mvc.perform(delete("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteAndCheckFakeId() throws Exception {
        Mockito.doThrow(new NotFoundException("User not found"))
                .when(service).delete(FAKE_ID);

        mvc.perform(delete("/users/" + FAKE_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
