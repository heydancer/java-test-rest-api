package com.heydancer.service.impl;

import com.heydancer.common.dto.ImageDTO;
import com.heydancer.exception.ForbiddenException;
import com.heydancer.exception.NotFoundException;
import com.heydancer.common.mapper.ImageMapper;
import com.heydancer.common.model.Image;
import com.heydancer.common.model.User;
import com.heydancer.repository.ImageRepository;
import com.heydancer.repository.UserRepository;
import com.heydancer.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service //Аннотация указывает, что класс представляет собой сервис для реализации бизнес-логики
@Transactional(readOnly = true) //Аннотация указывает, что методы сервиса выполняются в транзакции только для чтения
@RequiredArgsConstructor //Аннотация генерирует конструктор с 1 параметром для каждого поля, в данном коде используется для внедрения компонентов
public class ImageServiceImpl implements ImageService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    /*Метод add() добавляет изображение к пользователю и сохраняет его в базе данных.
    Если у пользователя уже есть изображение, выбрасывается исключение ForbiddenException.
    Метод также проверяет размер файла и преобразует его в объект Image при помощи ImageMapper.*/
    @Override
    @Transactional
    public ImageDTO add(Long userId, MultipartFile file) {
        User user = checkUser(userId);
        Image image = null;

        if (user.getImages().size() > 0) {
            throw new ForbiddenException("Image have already been added");
        }

        if (file.getSize() != 0) {
            try {
                image = imageMapper.toModel(file);
            } catch (IOException e) {
                throw new ForbiddenException("Image save error");
            }

            user.addImage(image);
        }

        return imageMapper.toDTO(Objects.requireNonNull(image));
    }

    /*Метод getById() получает объект Image по заданным userId и imageId путем вызова checkImageByOwner().*/
    @Override
    public Image getById(Long userId, Long imageId) {
        return checkImageByOwner(userId, imageId);
    }

    /*Метод update() обновляет информацию об изображении (имя файла, тип содержимого, размер)
    на основании переданного MultipartFile file.
    Метод также проверяет размер файла и преобразует его в объект Image при помощи ImageMapper.*/
    @Override
    @Transactional
    public ImageDTO update(Long userId, Long imageId, MultipartFile file) {
        Image imageFromDb = checkImageByOwner(userId, imageId);
        Image newImage;

        if (file.getSize() != 0) {
            try {
                newImage = imageMapper.toModel(file);
            } catch (IOException e) {
                throw new ForbiddenException("Image update error");
            }

            imageFromDb.setOriginalFileName(newImage.getOriginalFileName());
            imageFromDb.setContentType(newImage.getContentType());
            imageFromDb.setSize(newImage.getSize());
            imageFromDb.setBytes(newImage.getBytes());
        }

        return imageMapper.toDTO(imageFromDb);
    }

    /*Метод delete() удаляет заданное пользователем изображение путем вызова imageRepository.delete(image).*/
    @Override
    @Transactional
    public void delete(Long userId, Long imageId) {
        Image image = checkImageByOwner(userId, imageId);
        imageRepository.delete(image);
    }

    /*Метод checkUser(long userId) используется для получения пользователя по id
    через userRepository.findById(), если он существует; если нет - выбрасывается NotFoundException.*/
    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found. Id: %s", userId)));
    }

    /*Метод checkImageByOwner(long userId, long imageId) используется для получения объекта-изображения по id
    через imageRepository.findById(), если он существует; если нет - выбрасывается NotFoundException.
    Если пользователь не является владельцем изображения, выбрасывается исключение ForbiddenException.*/
    private Image checkImageByOwner(long userId, long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException(String.format("Image not found. Id: %s", imageId)));

        if (image.getUser().getId() != userId) {
            throw new ForbiddenException("User is not the owner of the image");
        } else {
            return image;
        }
    }
}
