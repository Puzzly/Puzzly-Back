package com.puzzly.api.coreComponent.ScheduleCore;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class NotifySchedule implements Job {
//  @Autowired
//  private final CalendarContentUserRelationJpaRepository calendarContentUserRelationJpaRepository;
//
//  public NotifyJob(
//      CalendarContentUserRelationJpaRepository calendarContentUserRelationJpaRepository) {
//    this.calendarContentUserRelationJpaRepository = calendarContentUserRelationJpaRepository;
//  }
public NotifySchedule() {
}

  // TODO: FE 에서 firebase fcm 키를 생성해주지 않으면 서버에 메세지 전송 테스트 불가능.
  // TODO: user에 fcm key save 하는 api 만들고 해당 사항 FE 전달하기
  // TODO: schedule 완료 후 다음 알람 시간 설정 & schedule 시간 update
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    Long calendarId = context.getMergedJobDataMap().getLong("calendarId");
    String memo = context.getMergedJobDataMap().getString("memo");
    String title = context.getMergedJobDataMap().getString("title");
    String body = context.getMergedJobDataMap().getString("body");
//    String token = context.getMergedJobDataMap().getString("token");

    System.out.println("body: " + body);

//    List<CalendarContentUserRelation> calendarUser = calendarContentUserRelationJpaRepository.findByCalendarId(calendarId);

    Message message = Message.builder()
        .putData("memo", memo)
        .putData("title", title)
        .putData("body", body)
//        .setToken(token)
        .build();

    try {
      String response = FirebaseMessaging.getInstance().send(message);
      System.out.println("Successfully sent message: " + response);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
