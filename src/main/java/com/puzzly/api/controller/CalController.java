package com.puzzly.api.controller;

import com.puzzly.api.dto.request.CalReq;
import com.puzzly.api.dto.response.CalRes;
import com.puzzly.api.service.CalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/cal")
@RequiredArgsConstructor
public class CalController {

    private final CalService calService;

    @PostMapping(value = "/join")
    public ResponseEntity<?> join(HttpServletRequest httpReq, @RequestBody CalReq calReq){
        CalRes calRes = calService.saveCal(calReq);
        return new ResponseEntity<>(calRes, HttpStatus.OK);
    }

    @GetMapping(value = "/findId")
    public ResponseEntity<?> findByCalId(
            HttpServletRequest httpReq, @RequestParam(name = "calId", required = false)String calId){
        return new ResponseEntity<>(calService.findById(calId), HttpStatus.OK);
    }
}
