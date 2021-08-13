package com.nikowis.kalah.rest;

import com.nikowis.kalah.exception.GameException;
import com.nikowis.kalah.exception.GameNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(value = {GameException.class})
    protected ResponseEntity<ErrorMessageDTO> handleGameException(GameException ex) {
        ErrorMessageDTO body = getExceptionResponseBody(ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(value = {GameNotFoundException.class})
    protected ResponseEntity<ErrorMessageDTO> handleGameNotFoundException(GameNotFoundException ex) {
        ErrorMessageDTO body = getExceptionResponseBody(ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    private ErrorMessageDTO getExceptionResponseBody(Exception ex) {
        return new ErrorMessageDTO(
                messageSource.getMessage(ex.getClass().getSimpleName(), null, LocaleContextHolder.getLocale())
        );
    }

    @Getter
    @AllArgsConstructor
    private static class ErrorMessageDTO {
        private final String message;
    }

}
