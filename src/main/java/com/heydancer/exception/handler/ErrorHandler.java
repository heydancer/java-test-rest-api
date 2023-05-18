package com.heydancer.exception.handler;

import com.heydancer.controller.ContactController;
import com.heydancer.controller.ImageController;
import com.heydancer.controller.UserController;
import com.heydancer.exception.ForbiddenException;
import com.heydancer.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class, ContactController.class, ImageController.class})
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException exception) {
        log.error("Arguments not found {}", exception.getMessage());

        return new ErrorResponse("NOT FOUND", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final ForbiddenException exception) {
        log.error("Invalid arguments {}", exception.getMessage());

        return new ErrorResponse("BAD REQUEST", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException exception) {
        log.error("Invalid arguments {}", exception.getMessage());

        return new ErrorResponse("BAD REQUEST", Objects.requireNonNull(exception.getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherException(final Exception exception) {
        log.error("Internal error {}", exception.getMessage(), exception);

        return new ErrorResponse("INTERNAL SERVER ERROR", exception.getMessage());
    }
}
