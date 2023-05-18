package com.heydancer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heydancer.common.dto.ImageDTO;
import com.heydancer.common.model.Image;
import com.heydancer.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class ImageControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ImageService imageService;
    private ImageDTO imageDTO;

    @BeforeEach
    void beforeEach() {
        imageDTO = ImageDTO.builder()
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .size(1L)
                .build();
    }

    @Test
    void shouldCreateAndReturnImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg",
                "test image content".getBytes());

        when(imageService.add(anyLong(), any(MultipartFile.class)))
                .thenReturn(imageDTO);

        mvc.perform(multipart("/users/2/images")
                        .file(file)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value(imageDTO.getFileName()))
                .andExpect(jsonPath("$.contentType").value(imageDTO.getContentType()))
                .andExpect(jsonPath("$.size").value(imageDTO.getSize()));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        byte[] bytes = Files.readAllBytes(Path.of("src/test/resources/test.jpg"));
        Image image = new Image();
        image.setOriginalFileName("test.jpg");
        image.setContentType(MediaType.IMAGE_JPEG_VALUE);
        image.setBytes(bytes);
        image.setSize((long) bytes.length);

        when(imageService.getById(3L, 4L))
                .thenReturn(image);

        MvcResult result =
                mvc.perform(get("/users/3/images/4"))
                        .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void shouldUpdateAndReturnImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg",
                "test image content".getBytes());

        String image = objectMapper.writeValueAsString(imageDTO);


        when(imageService.update(anyLong(), anyLong(), any(MultipartFile.class)))
                .thenReturn(imageDTO);

        mvc.perform(multipart("/users/2/images/3")
                        .file(file)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod(HttpMethod.PUT.name());
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(imageDTO.getFileName()))
                .andExpect(jsonPath("$.contentType").value(imageDTO.getContentType()))
                .andExpect(jsonPath("$.size").value(imageDTO.getSize()));
    }

    @Test
    public void shouldRemoveImage() throws Exception {
        mvc.perform(delete("/users/5/images/6")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(imageService, times(1))
                .delete(5L, 6L);
    }

}
