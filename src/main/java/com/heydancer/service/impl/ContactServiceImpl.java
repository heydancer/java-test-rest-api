package com.heydancer.service.impl;

import com.heydancer.common.dto.UserContactDTO;
import com.heydancer.common.model.User;
import com.heydancer.exception.ForbiddenException;
import com.heydancer.exception.NotFoundException;
import com.heydancer.repository.UserRepository;
import com.heydancer.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service //Аннотация указывает, что класс представляет собой сервис для реализации бизнес-логики
@Transactional(readOnly = true) //Аннотация указывает, что методы сервиса выполняются в транзакции только для чтения
@RequiredArgsConstructor //Аннотация генерирует конструктор с 1 параметром для каждого поля, в данном коде используется для внедрения компонентов
public class ContactServiceImpl implements ContactService {
    private final UserRepository userRepository;

    /*Метод add() добавляет контакты пользователя (email и phoneNumber) по его id,
    если они еще не были добавлены ранее. Если контакты уже есть, выбрасывается исключение ForbiddenException*/
    @Override
    @Transactional
    public UserContactDTO add(Long userId, UserContactDTO contact) {
        User user = checkUser(userId);

        String email = contact.getEmail();
        String phoneNumber = contact.getPhoneNumber();

        if (user.getEmail() == null && user.getPhoneNumber() == null) {
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
        } else {
            throw new ForbiddenException("Contacts have already been added");
        }

        return contact;
    }

    /*Метод getByUserId() получает контакты пользователя по его id.
    Если они не найдены, выбрасывается исключение NotFoundException.*/
    @Override
    public UserContactDTO getByUserId(Long userId) {
        User user = checkUser(userId);

        if (user.getEmail() == null && user.getPhoneNumber() == null) {
            throw new NotFoundException("Contacts not found");
        }

        String email = user.getEmail();
        String phoneNumber = user.getPhoneNumber();

        return UserContactDTO.builder()
                .email(email)
                .phoneNumber(phoneNumber)
                .build();
    }

    /*Метод update() обновляет контакты пользователя (email и phoneNumber) по его id, если они уже были добавлены ранее.
    Если нет - выбрасывается исключение ForbiddenException.*/
    @Override
    @Transactional
    public UserContactDTO update(Long userId, UserContactDTO contact) {
        User user = checkUser(userId);

        String email = contact.getEmail();
        String phoneNumber = contact.getPhoneNumber();

        if (user.getEmail() != null && user.getPhoneNumber() != null) {
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
        } else {
            throw new ForbiddenException("Contacts have not been added yet");
        }

        return contact;
    }

    /*Метод delete() удаляет все контакты пользователя (email и phoneNumber) по его id.*/
    @Override
    @Transactional
    public void delete(Long userId) {
        User user = checkUser(userId);

        user.setEmail(null);
        user.setPhoneNumber(null);
    }

    /*Метод checkUser(long userId) проверяет существование пользователя в базе данных userRepository.
    Если пользователь не найден выбрасывается исключение NotFoundException*/
    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found. Id: %s", userId)));
    }
}
