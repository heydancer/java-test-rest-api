package com.heydancer.common.mapper;

import com.heydancer.common.dto.ImageDTO;
import com.heydancer.common.model.Image;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ImageMapper {
    public Image toModel(MultipartFile file) throws IOException {
        return Image.builder()
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .bytes(file.getBytes())
                .build();
    }

    public ImageDTO toDTO(Image image) {
        return ImageDTO.builder()
                .fileName(image.getOriginalFileName())
                .contentType(image.getContentType())
                .size(image.getSize())
                .build();
    }
}
