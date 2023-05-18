package com.heydancer.controller;

import com.heydancer.common.dto.ImageDTO;
import com.heydancer.common.model.Image;
import com.heydancer.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ImageDTO saveImage(@PathVariable Long userId, @RequestPart MultipartFile file) {
        log.info("User with id: {} adding image", userId);

        return imageService.add(userId, file);
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<?> getImage(@PathVariable Long userId, @PathVariable Long imageId) {
        log.info("Getting image. User id: {}, image id: {}", userId, imageId);

        Image image = imageService.getById(userId, imageId);
        return ResponseEntity.ok()
                .header("fileName", image.getOriginalFileName())
                .contentType(MediaType.valueOf(image.getContentType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }

    @PutMapping("/{imageId}")
    public ImageDTO updateImage(@PathVariable Long userId, @PathVariable Long imageId, @RequestPart MultipartFile file) {
        log.info("Updating image. User id: {}, image id: {}", userId, imageId);

        return imageService.update(userId, imageId, file);
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeImage(@PathVariable Long userId, @PathVariable Long imageId) {
        log.info("Removing image. User id: {}, image id: {}", userId, imageId);

        imageService.delete(userId, imageId);
    }
}
