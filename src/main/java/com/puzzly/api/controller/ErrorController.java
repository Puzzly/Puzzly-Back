package com.puzzly.api.controller;

import com.puzzly.api.exception.FailException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ErrorController {

    @RequestMapping(value = "/error", method = RequestMethod.POST)
    public ResponseEntity<?> error(HttpServletRequest request, HttpServletResponse response)  {
        int status = Integer.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString());
        String message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE).toString();
        return new ResponseEntity<>(new FailException(message, status), HttpStatus.valueOf(status));
    }
}
