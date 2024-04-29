package com.puzzly.api.controller;

import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.dto.response.UserResponseDto;
import com.puzzly.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> joinUser(
            HttpServletRequest request,
            @RequestBody UserRequestDto userRequestDto
            ){
        UserResponseDto userDTOResponse = userService.insertUser(userRequestDto);
        return new ResponseEntity<>(userDTOResponse, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<?> getUser(
            HttpServletRequest request,
            @RequestParam (required=false) Long userId
    ){
        List<UserResponseDto> userResponseDtoList = userService.selectUser(userId);
        return new ResponseEntity<>(userResponseDtoList, HttpStatus.OK);
    }



    @GetMapping("/mybatis")
    public ResponseEntity<?> getUserMybatis(
            HttpServletRequest request,
            @RequestParam (required=false) Long userId
    ){
        List<UserResponseDto> userResponseDtoList = userService.selectUserMybatis(userId);
        return new ResponseEntity<>(userResponseDtoList, HttpStatus.OK);
    }
}
