package com.puzzly.api.coreComponent;

import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.dto.request.CalendarRequestDto;
import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.service.CalendarService;
import com.puzzly.api.service.UserService;
import com.puzzly.api.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.MapUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationListenerService implements ApplicationListener<ContextRefreshedEvent> {

    private final UserService userService;
    private final CalendarService calendarService;

    private final JwtUtils jwtUtils;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("[++] Application ready");
        log.error("jwt key : " + jwtUtils.getJwtSecretKey());

        try {
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
            userService.createUser(user);
        }catch(FailException e){
            if(e.getMessage().equals("SERVER_MESSAGE_EMAIL_ALREADY_EXISTS")){

            }
        }
        SecurityUser securityUser = new SecurityUser();
        securityUser.setUser(userService.findById((long)1).orElse(null));
        Map initCal = calendarService.getCalendarList(securityUser, 0, 1, false);
        List calendars = (List) MapUtils.getObject(initCal, "calendarList");

        if(calendars.isEmpty()) {
            CalendarRequestDto calendarRequestDto = new CalendarRequestDto();
            calendarRequestDto.setCalendarName("PuzzlyCalendar");
            calendarService.createCalendar(securityUser, calendarRequestDto);
        }

    }
}
