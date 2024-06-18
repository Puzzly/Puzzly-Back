package com.puzzly.api.service;

import com.puzzly.api.coreComponent.ScheduleCore.NotifySchedule;
import com.puzzly.api.entity.CalendarContent;
import com.puzzly.api.repository.jpa.CalendarContentJpaRepository;
import com.puzzly.api.repository.jpa.CalendarContentUserRelationJpaRepository;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

  private final CalendarContentJpaRepository calendarContentJpaRepository;
  private final CalendarContentUserRelationJpaRepository calendarContentUserRelationJpaRepository;

  @Autowired
  private Scheduler scheduler;

  /* 알림 내역 get */
//  public void scheduleAlarms() throws SchedulerException {
//    List<CalendarContent> calendarContents = calendarContentJpaRepository.findByIsNotifyTrue();
//    for (CalendarContent content : calendarContents) {
//      scheduleAlarm(content);
//    }
//  }

  // 알림 job 등록
  public void scheduleAlarm(CalendarContent content) throws SchedulerException {
    JobDetail jobDetail = JobBuilder.newJob(NotifySchedule.class)
        .usingJobData("calendarId", content.getCalendar().getCalendarId())
        .usingJobData("memo", content.getMemo())
        .usingJobData("title", "calendar_content_notify_title")
        .usingJobData("body", "calendar_content_notify_body")
        .build();

    Trigger trigger = TriggerBuilder.newTrigger()
        .startAt(Date.from(content.getNotifyDate().atZone(ZoneId.systemDefault()).toInstant()))
        .build();

    scheduler.scheduleJob(jobDetail, trigger);
  }

}
