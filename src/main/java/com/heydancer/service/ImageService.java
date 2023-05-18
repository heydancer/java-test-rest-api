package com.heydancer.service;

import com.heydancer.common.dto.ImageDTO;
import com.heydancer.common.model.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    ImageDTO add(Long userId, MultipartFile file);

    Image getById(Long userId, Long imageId);

    ImageDTO update(Long userId, Long imageId, MultipartFile file);

    void delete(Long userId, Long imageId);


}
