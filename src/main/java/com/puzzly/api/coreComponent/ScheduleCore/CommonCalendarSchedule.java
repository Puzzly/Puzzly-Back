package com.puzzly.api.coreComponent.ScheduleCore;

import com.puzzly.api.service.CalendarService;
import com.puzzly.api.util.CustomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@EnableScheduling
@Component
@Log4j2
@RequiredArgsConstructor
public class CommonCalendarSchedule {

    private final CalendarService calendarService;

    private final CustomUtils customUtils;
    private int pullingDuration = 5;

    @Scheduled(cron = "0 1 1 1,15 * *")
    public void pullOpenCalendarSchedule() {
        LocalDate currentDate = LocalDate.now();
        long startmils = System.currentTimeMillis();
        log.info("[+] SCHEDULE CALL : pullOpenCalendarSchedule " + customUtils.localDateTimeStringNow());
        calendarService.pullOpenCalendarSchedule(currentDate, pullingDuration);
        log.info("[+] SCHEDULE END : pullOpenCalendarSchedule AT" + customUtils.localDateTimeStringNow() + ", USED : " + (System.currentTimeMillis() - startmils) + " ms");

    }
}
