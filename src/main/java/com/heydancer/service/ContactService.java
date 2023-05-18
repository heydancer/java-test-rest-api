package com.heydancer.service;

import com.heydancer.common.dto.UserContactDTO;

public interface ContactService {
    UserContactDTO add(Long userId, UserContactDTO contact);

    UserContactDTO getByUserId(Long userId);

    UserContactDTO update(Long userId, UserContactDTO contact);

    void delete(Long userId);
}
