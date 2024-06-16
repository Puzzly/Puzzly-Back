package com.puzzly.api.coreComponent;

import com.puzzly.api.domain.AccountAuthority;
import com.puzzly.api.domain.SecurityUser;
import com.puzzly.api.dto.request.CalendarContentRequestDto;
import com.puzzly.api.dto.request.CalendarLabelRequestDto;
import com.puzzly.api.dto.request.CalendarRequestDto;
import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.dto.response.CalendarLabelResponseDto;
import com.puzzly.api.entity.Calendar;
import com.puzzly.api.entity.CalendarContent;
import com.puzzly.api.entity.CalendarLabel;
import com.puzzly.api.entity.User;
import com.puzzly.api.exception.FailException;
import com.puzzly.api.service.CalendarService;
import com.puzzly.api.service.UserService;
import com.puzzly.api.util.CustomUtils;
import com.puzzly.api.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.MapUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationListenerService implements ApplicationListener<ContextRefreshedEvent> {

    private final UserService userService;
    private final CalendarService calendarService;

    private final JwtUtils jwtUtils;
    private final CustomUtils customUtils;
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

        Boolean initFlag = calendars.isEmpty();
        if(initFlag) {

            CalendarRequestDto calendarRequestDto = new CalendarRequestDto();
            calendarRequestDto.setCalendarName("PuzzlyCalendar");
            calendarService.createCalendar(securityUser, calendarRequestDto);

            Calendar calendar = calendarService.getCalendarForDummy((long)1);

            CalendarLabelRequestDto calendarLabelRequestDto = new CalendarLabelRequestDto();
            calendarLabelRequestDto.setCalendarId(calendar.getCalendarId());
            calendarLabelRequestDto.setLabelName("더미데이터라벨!");
            calendarLabelRequestDto.setColorCode("#000000");
            calendarLabelRequestDto.setOrderNum(1);
            calendarService.createCalendarLabel(securityUser, calendarLabelRequestDto);

            setupRequestedCalendarContentsDummyData(calendar, securityUser);
        }

    }

    public void setupRequestedCalendarContentsDummyData(Calendar calendar, SecurityUser securityUser){

        ArrayList<LocalDateTime> startDateTime = new ArrayList<>();
        startDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-06-14 00:00:00"));
        startDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-06-14 00:00:00"));
        startDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-06-22 10:00:00"));
        startDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-06-22 15:00:00"));
        startDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-05-21 10:00:00"));
        startDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-07-13 14:00:00"));
        startDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-07-14 00:00:00"));

        ArrayList<LocalDateTime> endDateTime = new ArrayList<>();
        endDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-06-14 23:59:59"));
        endDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-06-15 23:59:59"));
        endDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-06-22 18:00:00"));
        endDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-06-22 22:00:00"));
        endDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-05-23 15:00:00"));
        endDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-07-13 23:59:59"));
        endDateTime.add(customUtils.localDateTimeFromDateTimeString("2024-07-15 23:59:59"));

        ArrayList<String> contentTitle = new ArrayList<>();
        contentTitle.add("퍼즐리 회의");
        contentTitle.add("국내여행(경주)");
        contentTitle.add("알고리즘 스터디");
        contentTitle.add("통장잔고 확인하고 전화하기");
        contentTitle.add("국내여행(강원도)");
        contentTitle.add("출장(춘천)");
        contentTitle.add("국내여행(파주)");

        ArrayList tem = (ArrayList) MapUtils.getObject(calendarService.getCalendarLabelList(securityUser, (long)1, 0, 10), "calendarList");

        CalendarLabelResponseDto label = (CalendarLabelResponseDto) tem.get(0);

        for(int i=0; i<startDateTime.size(); i++){
            CalendarContentRequestDto content = CalendarContentRequestDto.builder()
                    .calendarId(calendar.getCalendarId())
                    .startDateTime(startDateTime.get(i))
                    .endDateTime(endDateTime.get(i))
                    .title(contentTitle.get(i))
                    .isRecurrable(false)
                    .memo(contentTitle.get(i))
                    .isNotify(false)
                    .labelId(label.getLabelId())
                    .createUserIdList(new ArrayList<>(){{add(securityUser.getUser().getUserId());}})
                    .build();
            calendarService.createCalendarContent(securityUser, content);
        }

    }
}
