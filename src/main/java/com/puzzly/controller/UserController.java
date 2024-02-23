package com.puzzly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzly.dto.UserDTORequest;
import com.puzzly.dto.UserDTOResponse;
import com.puzzly.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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

    @PostMapping()
    public ResponseEntity<?> postUser(
            HttpServletRequest request,
            @RequestBody UserDTORequest userDTO
    ){
        UserDTOResponse userDTOResponse = userService.insertUser(userDTO);
        return new ResponseEntity<>(userDTOResponse, HttpStatus.OK);
    }

    @GetMapping(value="/all")
    public ResponseEntity<?> getAllUser(
            HttpServletRequest request
    ){
        List<UserDTORequest> list = userService.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
