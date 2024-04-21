package com.puzzly.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/test")
public class TestUserEndpoint {

    @GetMapping("/user")
    public ResponseEntity<?> getHello(){
        return new ResponseEntity<>("hello", HttpStatus.OK);
    }
}
