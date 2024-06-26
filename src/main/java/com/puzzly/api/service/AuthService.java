package com.puzzly.api.service;


import com.puzzly.api.entity.JwtToken;
import com.puzzly.api.entity.User;

import com.puzzly.api.exception.FailException;
import com.puzzly.api.repository.jpa.AuthJpaRepository;
import com.puzzly.api.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthJpaRepository authRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public JwtToken refreshJwtToken(String accessToken, String refreshToken) throws FailException {
        if(StringUtils.isEmpty(accessToken)){
            throw new FailException("access Token not exists", HttpStatus.BAD_REQUEST.value());
        }

        if (!accessToken.startsWith("Bearer ")) {
            log.error("token invalid");
            throw new FailException("Token Invalid", 400);
        }
        accessToken = accessToken.split(" ")[1];
        JwtToken newToken = new JwtToken();

        JwtToken expiredJwtToken = selectJwtToken(accessToken, refreshToken);
        // TODO Check : Token만 만들껀데, PW가 메모리상에 아예 안올라오는게 맞는가?
        User user = userService.findByEmail(expiredJwtToken.getEmail());

        if(user != null){

            //newToken.setAccessToken(jwtUtils.generateJwtToken(user));
            newToken.setRefreshToken(jwtUtils.generateRefreshToken("refreshToken"));
            newToken.setEmail(user.getEmail());

            authRepository.delete(expiredJwtToken);
            authRepository.save(newToken);
        }
        newToken.setAccessToken("Bearer " + newToken.getAccessToken());
        return newToken;
    }


    public JwtToken insertJwtToken(JwtToken jwtToken){
        return authRepository.save(jwtToken);
    }

    public JwtToken selectJwtToken(String accessToken, String refreshToken){
        return authRepository.findByAccessTokenAndRefreshToken(accessToken, refreshToken);
    }


}
