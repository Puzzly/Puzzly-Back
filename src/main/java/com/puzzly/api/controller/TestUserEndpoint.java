package com.puzzly.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/test")
@Tag(name="99.Authority Check endpoint - user")
public class TestUserEndpoint {

    @GetMapping("/user")
    public ResponseEntity<?> getHello(){
        return new ResponseEntity<>("hello", HttpStatus.OK);
    }
}
