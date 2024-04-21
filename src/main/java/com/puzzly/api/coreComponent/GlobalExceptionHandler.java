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

@ControllerAdvice
@RestControllerAdvice
@Log4j2
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(Exception e, HttpServletRequest req) throws JsonMappingException, JsonProcessingException {
        HashMap<String, Object> eMap = objectMapper.readValue(e.getMessage(), HashMap.class);
        HttpStatus status =HttpStatus.valueOf((int)eMap.get("code"));
        String message = (String)eMap.get("message");

        e.printStackTrace();
        log.error(e);

        FailException failException = new FailException(message, status.value());
        return new ResponseEntity<>(failException, HttpStatus.BAD_REQUEST);
    }
}
