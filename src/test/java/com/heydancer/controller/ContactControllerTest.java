package com.heydancer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heydancer.common.dto.UserContactDTO;
import com.heydancer.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class ContactControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ContactService contactService;
    private UserContactDTO userContactDTO;

    @BeforeEach
    void beforeEach() {
        userContactDTO = UserContactDTO.builder()
                .email("test@mail.ru")
                .phoneNumber("89992221111")
                .build();
    }

    @Test
    void shouldCreateAndReturnContact() throws Exception {
        String userContact = objectMapper.writeValueAsString(userContactDTO);

        when(contactService.add(anyLong(), any(UserContactDTO.class)))
                .thenReturn(userContactDTO);

        mvc.perform(post("/users/1/contacts")
                        .with(csrf())
                        .content(userContact)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(userContact));
    }

    @Test
    void shouldReturnContactByUserId() throws Exception {
        String userContact = objectMapper.writeValueAsString(userContactDTO);

        when(contactService.getByUserId(1L))
                .thenReturn(userContactDTO);

        mvc.perform(get("/users/1/contacts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(userContact));
    }

    @Test
    void shouldUpdateAndReturnContact() throws Exception {
        String userContact = objectMapper.writeValueAsString(userContactDTO);

        when(contactService.update(anyLong(), any(UserContactDTO.class)))
                .thenReturn(userContactDTO);

        mvc.perform(put("/users/1/contacts")
                        .with(csrf())
                        .content(userContact)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(userContact));
    }

    @Test
    void shouldUpdateAndCheckIncorrectEmail() throws Exception {
        userContactDTO.setEmail("INCORRECT EMAIL");
        String userContact = objectMapper.writeValueAsString(userContactDTO);

        when(contactService.update(1L, userContactDTO))
                .thenThrow(new RuntimeException());

        mvc.perform(put("/users/1/contacts")
                        .with(csrf())
                        .content(userContact)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteContactByUserId() throws Exception {
        mvc.perform(delete("/users/1/contacts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}