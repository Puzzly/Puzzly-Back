package com.puzzly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzly.dto.UserDTORequest;
import com.puzzly.dto.UserDTOResponse;
import com.puzzly.entity.User;
import com.puzzly.security.details.SecurityUser;
import com.puzzly.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping()
    public ResponseEntity<?> getUser(
            HttpServletRequest request,
            @RequestParam(name = "userName", required=false) String userName,
            @RequestParam(name = "userId", required=false) Long userId
    ) {
        UserDTOResponse userDTOResponse = userService.getUser(userName);
        return new ResponseEntity<>(userDTOResponse, HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinUser(
            HttpServletRequest request,
            @RequestBody UserDTORequest userDTO
    ){
        UserDTOResponse userDTOResponse = userService.insertUser(userDTO);
        return new ResponseEntity<>(userDTOResponse, HttpStatus.OK);
    }
    @GetMapping(value="/test/user")
    public ResponseEntity<?> userLoginTest(
            HttpServletRequest request
    ){
        return new ResponseEntity<>("Hello User!", HttpStatus.OK);
    }
    @GetMapping(value="/test/admin")
    public ResponseEntity<?> adminLoginTest(
            HttpServletRequest request
    ){
        return new ResponseEntity<>("Hello Admin!", HttpStatus.OK);
    }
}
