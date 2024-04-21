package com.puzzly.api.coreComponent;

import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.dto.request.UserExRequestDto;
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
        // test용 ADMIN 추가
        // TODO 나중에  DTO 만들면 set쪽 활성화해서 주석풀기
        UserRequestDto user = new UserRequestDto();
        user.setEmail("admin@puzzly.com");
        user.setPassword("admin");
        user.setBirth(LocalDate.parse("1994-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        user.setAccountAuthority(AccountAuthority.ROLE_ADMIN);
        user.setUserExRequestDto(new UserExRequestDto());
        userService.insertUser(user);

    }
}
