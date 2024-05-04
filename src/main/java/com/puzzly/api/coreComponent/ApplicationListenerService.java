package com.puzzly.api.coreComponent;

import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.service.UserService;
import com.puzzly.api.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationListenerService implements ApplicationListener<ContextRefreshedEvent> {

    private final UserService userService;

    private final JwtUtils jwtUtils;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("[++] Application ready");
        log.error("jwt key : " + jwtUtils.getJwtSecretKey());

        UserRequestDto user = new UserRequestDto();
        user.setUserName("admin");
        user.setNickName("관리자");
        user.setEmail("admin@puzzly.com");
        user.setPassword("admin");
        user.setPhoneNumber("010-1111-2222");
        user.setBirth(LocalDate.parse("1994-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        user.setAccountAuthority(AccountAuthority.ROLE_ADMIN);
        user.setFirstTermAgreement(true);
        user.setSecondTermAgreement(true);
        user.setGender(true);
        userService.insertUser(user);

    }
}
