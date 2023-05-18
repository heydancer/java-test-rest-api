package com.heydancer.controller;

import com.heydancer.common.dto.UserContactDTO;
import com.heydancer.service.ContactService;
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

@Slf4j
@RestController
@RequestMapping("/users/{userId}/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserContactDTO createContact(@PathVariable Long userId, @Valid @RequestBody UserContactDTO contact) {
        log.info("Create contact. User id: {}, contact: {}", userId, contact);

        return contactService.add(userId, contact);
    }

    @GetMapping
    public UserContactDTO getContact(@PathVariable Long userId) {
        log.info("Getting contact. User id: {}", userId);

        return contactService.getByUserId(userId);
    }

    @PutMapping
    public UserContactDTO updateContact(@PathVariable Long userId, @Valid @RequestBody UserContactDTO contact) {
        log.info("Updating contact. User id: {}, contact: {}", userId, contact);

        return contactService.update(userId, contact);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeContact(@PathVariable Long userId) {
        log.info("Removing contact by id {}", userId);

        contactService.delete(userId);
    }
}
