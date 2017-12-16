package com.sebis.cepengineservice.controller.exception;

import com.sebis.cepengineservice.service.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    ErrorMessage handle(ValidationException exception) {
        return toErrorMessage(exception);
    }

    private ErrorMessage toErrorMessage(Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ErrorMessage(exception.getMessage());
    }

}
