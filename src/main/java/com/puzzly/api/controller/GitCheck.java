package com.puzzly.api.controller;

import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.dto.response.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/git")
public class GitCheck {
    @GetMapping("/check")
    public ResponseEntity<?> joinUser(
            HttpServletRequest request
    ){
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }
}
