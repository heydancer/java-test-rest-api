package com.heydancer.common.mapper;

import com.heydancer.common.dto.FullUserDTO;
import com.heydancer.common.dto.ShortUserDTO;
import com.heydancer.common.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public User toModel(ShortUserDTO userDTO) {
        return User.builder()
                .firstName(userDTO.getFirstname())
                .lastName(userDTO.getLastname())
                .surname(userDTO.getSurname())
                .birthday(userDTO.getBirthday())
                .build();
    }

    public FullUserDTO toFullDTO(User user) {
        return FullUserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .surname(user.getSurname())
                .birthday(user.getBirthday())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public ShortUserDTO toShortDTO(User user) {
        return ShortUserDTO.builder()
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .surname(user.getSurname())
                .birthday(user.getBirthday())
                .build();
    }

    public List<ShortUserDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(this::toShortDTO)
                .collect(Collectors.toList());
    }
}
