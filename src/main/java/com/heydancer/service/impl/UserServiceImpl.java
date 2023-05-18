package com.heydancer.service.impl;

import com.heydancer.common.dto.FullUserDTO;
import com.heydancer.common.dto.ShortUserDTO;
import com.heydancer.exception.NotFoundException;
import com.heydancer.common.mapper.UserMapper;
import com.heydancer.common.model.User;
import com.heydancer.repository.UserRepository;
import com.heydancer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service //Аннотация указывает, что класс представляет собой сервис для реализации бизнес-логики
@Transactional(readOnly = true) //Аннотация указывает, что методы сервиса выполняются в транзакции только для чтения
@RequiredArgsConstructor
//Аннотация генерирует конструктор с 1 параметром для каждого поля, в данном коде используется для внедрения компонентов
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /* Метод add() добавляет нового пользователя на основании объекта ShortUserDTO при помощи UserMapper.toModel().
     Метод сохраняет пользователя в базе данных и преобразует его обратно в FullUserDTO с помощью UserMapper.toFullDTO().*/
    @Override
    @Transactional
    public FullUserDTO add(ShortUserDTO shortUserDTO) {
        User user = userMapper.toModel(shortUserDTO);

        return userMapper.toFullDTO(userRepository.save(user));
    }

    /*Метод getById() получает объект User по заданному userId путем вызова метода checkUser().*/
    @Override
    public FullUserDTO getById(Long userId) {
        User user = checkUser(userId);

        return userMapper.toFullDTO(user);
    }

    /* Метод getAll() возвращает список пользователей и преобразует в List<ShortUserDTO> при помощи UserMapper.toDTOList().*/
    @Override
    public List<ShortUserDTO> getAll() {
        return userMapper.toDTOList(userRepository.findAll());
    }

    /*Метод update() обновляет информацию о пользователе (Имя, Фамилия, Отчество, Дата Рождения)
    Метод также проверяет есть ли такой пользователь в базе данных с помощью метода checkUser.
    Метод сохраняет пользователя в базе данных и преобразует его обратно в FullUserDTO с помощью UserMapper.toFullDTO().*/
    @Override
    @Transactional
    public FullUserDTO update(Long userId, ShortUserDTO shortUserDTO) {
        User user = checkUser(userId);

        String firstName = shortUserDTO.getFirstname();
        String lastName = shortUserDTO.getLastname();
        String surname = shortUserDTO.getSurname();
        LocalDate birthday = shortUserDTO.getBirthday();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setSurname(surname);
        user.setBirthday(birthday);

        return userMapper.toFullDTO(user);
    }

    /*Метод delete() удаляет пользователя путем вызова userRepository.delete(user).*/
    @Override
    @Transactional
    public void delete(Long userId) {
        User user = checkUser(userId);

        userRepository.delete(user);
    }

    /* Метод checkUser(long userId) используется для получения пользователя по id через userRepository.findById(),
     если он существует; если нет - выбрасывается NotFoundException.*/
    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found. Id: %s", userId)));
    }
}
