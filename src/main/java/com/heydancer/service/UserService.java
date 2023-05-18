package com.heydancer.service;

import com.heydancer.common.dto.FullUserDTO;
import com.heydancer.common.dto.ShortUserDTO;

import java.util.List;

public interface UserService {
    FullUserDTO add(ShortUserDTO shortUserDTO);

    FullUserDTO getById(Long userId);

    List<ShortUserDTO> getAll();

    FullUserDTO update(Long userId, ShortUserDTO shortUserDTO);

    void delete(Long userId);
}
