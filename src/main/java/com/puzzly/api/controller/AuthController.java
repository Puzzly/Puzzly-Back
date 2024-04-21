package com.puzzly.api.controller;

import com.puzzly.api.entity.JwtToken;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Tag(name="00. token refresh", description = "token 재발급")
    @PostMapping("/refresh")
    @Operation(summary = " token refresh", description = "토큰 재발급, header에 만료된 토큰/ body에 refresh 토큰 필요 ** 현시점, Response에 RefreshToken과 AccessToken만 유효")
    @ApiResponse(responseCode = "200", description = "게시글 조회 성공", content = @Content(schema = @Schema(implementation = JwtToken.class)))
    public ResponseEntity<?> refreshToken(
            HttpServletRequest request,
            @RequestParam(name="refreshToken") String refreshToken
            //@RequestBody(description="refreshToken", required=true) String refreshToken
    ) throws FailException {
        String accessToken = request.getHeader("Authorization");
        JwtToken jwtToken = authService.refreshJwtToken(accessToken, refreshToken);

        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
    }
}
