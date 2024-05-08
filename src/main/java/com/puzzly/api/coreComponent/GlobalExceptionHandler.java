package com.puzzly.api.coreComponent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzly.api.exception.FailException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(FailException.class)
    protected ResponseEntity<?> handleFailException(FailException e, HttpServletRequest req) throws JsonMappingException, JsonProcessingException {
        HttpStatus status = HttpStatus.valueOf(e.getStatus());
        e.printStackTrace();
        ResponseEntity res = new ResponseEntity(e, status);
        return res;
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        log.error(e);
        return new ResponseEntity<>(new FailException(e), HttpStatus.BAD_REQUEST);
    }

}
