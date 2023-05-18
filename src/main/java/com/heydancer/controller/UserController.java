package com.heydancer.controller;

import com.heydancer.common.dto.FullUserDTO;
import com.heydancer.common.dto.ShortUserDTO;
import com.heydancer.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullUserDTO createUser(@Valid @RequestBody ShortUserDTO user) {
        log.info("Create user: {}", user);

        return userService.add(user);
    }

    @GetMapping("/{userId}")
    public FullUserDTO getUser(@PathVariable Long userId) {
        log.info("Getting user by id: {}", userId);

        return userService.getById(userId);
    }

    @GetMapping
    public List<ShortUserDTO> getUsers() {
        log.info("Getting all users");

        return userService.getAll();
    }

    @PutMapping("/{userId}")
    public FullUserDTO updateUser(@PathVariable Long userId, @Valid @RequestBody ShortUserDTO user) {
        log.info("Updating user by id: {}", userId);

        return userService.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable Long userId) {
        log.info("Removing user by id {}", userId);

        userService.delete(userId);
    }
}
