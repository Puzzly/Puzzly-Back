package com.puzzly.api.coreComponent.securityCore.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.entity.JwtToken;

import com.puzzly.api.service.AuthService;
import com.puzzly.api.service.UserService;

import com.puzzly.api.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomUsernamePasswordSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {


    private final UserService userService;

    private final ObjectMapper objectMapper;

    private final JwtUtils jwtUtils;

    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        log.info("Auth succeed!");
        SecurityUser securityUser = (SecurityUser)authentication.getPrincipal();

        String accessToken = jwtUtils.generateJwtToken(securityUser.getUser());
        String refreshToken = jwtUtils.generateRefreshToken("refreshToken");

        response.setContentType("application/json");
        response.setStatus(HttpStatus.OK.value());

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("accessToken", "Bearer " + accessToken );
        responseMap.put("refreshToken", refreshToken);

        JwtToken jwtToken = new JwtToken(securityUser.getEmail(), accessToken, refreshToken);
        authService.insertJwtToken(jwtToken);

        JsonNode object = objectMapper.valueToTree(responseMap);

        try (PrintWriter printWriter = response.getWriter()){
            printWriter.print(object);
            printWriter.flush();
        }

        /*
        // cookie 방식으로 처리 실패
        JSONObject userInfo = new JSONObject(objectMapper.writeValueAsString(user));
        HashMap<String, Object> responseMap = new HashMap<>();
        JSONObject object;

        responseMap.put("userInfo", userInfo);
        responseMap.put("resultCode", 200);
        object = new JSONObject(responseMap);

        String token = JwtUtils.generateJwtToken(user);
        object.put("token", token);

        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        log.error("res: " + response.toString());
        try (PrintWriter printWriter = response.getWriter()){
            printWriter.print(object);
            printWriter.flush();
        }

         */
    }
}
