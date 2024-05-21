package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarContentAttachmentsResponseDto;
import com.puzzly.api.dto.response.CalendarContentRecurringInfoResponseDto;
import com.puzzly.api.dto.response.CalendarContentResponseDto;
import com.puzzly.api.entity.QCalendar;
import com.puzzly.api.entity.QCalendarContent;
import com.puzzly.api.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.puzzly.api.entity.QCalendar.calendar;

@RequiredArgsConstructor
public class CalendarContentJpaRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;

    // TODO join 없이 무슨쿼리 나갈까? -> scala subquery는 자체적으로 innerjoin으로 파싱되서 나간다.
    public List<CalendarContentResponseDto> selectCalendarContentByDateTimeAndCalendar(Long userId, Long calendarId, LocalDateTime startDateTime, LocalDateTime limitStartDateTime, boolean isDeleted){
        QUser qCreateUser = new QUser("createUser");
        QUser qModifyUser = new QUser("modifyUser");
        QCalendar calendar = QCalendar.calendar;
        QCalendarContent content = new QCalendarContent("content");
        return jpaQueryFactory
                .select(Projections.fields(CalendarContentResponseDto.class,
                        content.contentId, content.calendar.calendarId, calendar.calendarName, content.createUser.userId.as("create_id"), content.createUser.nickName.as("createNickName"),
                        content.modifyUser.userId.as("modifyId"), content.modifyUser.nickName.as("modifyNickName"),
                        content.startDateTime, content.endDateTime,
                        content.memo, content.notify, content.location, content.createDateTime, content.modifyDateTime))
                .from(content)
                .leftJoin(calendar).on(content.calendar.calendarId.eq(calendar.calendarId))
                .leftJoin(qCreateUser).on(content.createUser.userId.eq(qCreateUser.userId))
                .leftJoin(qModifyUser).on(content.modifyUser.userId.eq(qModifyUser.userId))
                .where(eqCalendar(calendarId))
                .fetch();
    }
    public List<CalendarContentAttachmentsResponseDto> selectCalendarContentAttachmentsByContentId(Long contentId, Boolean isDeleted){
        return null;
    }

    public CalendarContentRecurringInfoResponseDto selectCalendarContentRecurringInfo(Long contentId, Boolean isDeleted){
        return null;
    }
    public CalendarContentResponseDto selectCalendarContentByContentId(Long contentId, Boolean isDeleted){
        return null;
    }

    private BooleanExpression eqCalendar(Long calendarId){
        if(ObjectUtils.isEmpty(calendarId)){
            return null;
        }
        return calendar.calendarId.eq(calendarId);
    }
}
