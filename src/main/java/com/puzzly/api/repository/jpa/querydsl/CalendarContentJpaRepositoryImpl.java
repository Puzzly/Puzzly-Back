package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarContentResponseDto;
import com.puzzly.api.entity.QCalendar;
import com.puzzly.api.entity.QCalendarContent;
import com.puzzly.api.entity.QCalendarUserRelation;
import com.puzzly.api.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.puzzly.api.entity.QCalendar.calendar;

@RequiredArgsConstructor
public class CalendarContentJpaRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;

    // TODO join 없이 무슨쿼리 나갈까? -> scala subquery는 자체적으로 innerjoin으로 파싱되서 나간다.
    public List<CalendarContentResponseDto> selectCalendarContentByDateTimeAndCalendar(Long userId, Long calendarId, LocalDateTime startDateTime, LocalDateTime limitStartDateTime, boolean isDeleted){
        QUser createUser = new QUser("createUser");
        QUser modifyUser = new QUser("modifyUser");
        QCalendar calendar = QCalendar.calendar;
        QCalendarContent content = new QCalendarContent("content");
        return jpaQueryFactory
                .select(Projections.fields(CalendarContentResponseDto.class,
                        content.contentId, content.calendar.calendarId, calendar.calendarName,
                        createUser.userId.as("create_id"), createUser.nickName.as("createNickName"),
                        modifyUser.userId.as("modifyId"), modifyUser.nickName.as("modifyNickName"),
                        content.startDateTime, content.endDateTime,
                        content.memo, content.isNotify, content.location, content.createDateTime, content.modifyDateTime))
                .from(content)
                .leftJoin(calendar).on(content.calendar.calendarId.eq(calendar.calendarId))
                .leftJoin(createUser).on(content.createUser.userId.eq(createUser.userId))
                .leftJoin(modifyUser).on(content.modifyUser.userId.eq(modifyUser.userId))
                .where(content.isDeleted.eq(isDeleted), eqCalendar(calendarId))
                .fetch();
    }
    public List<CalendarContentResponseDto> selectCalendarContentByDateTime(Long userId, LocalDateTime startDateTime, LocalDateTime limitStartDateTime, boolean isDeleted){
        QUser createUser = new QUser("createUser");
        QUser modifyUser = new QUser("modifyUser");
        QCalendar calendar = QCalendar.calendar;
        QCalendarContent content = new QCalendarContent("content");
        QCalendarUserRelation cur = new QCalendarUserRelation("cur");
        return jpaQueryFactory
                .select(Projections.fields(CalendarContentResponseDto.class,
                        content.contentId, content.calendar.calendarId, calendar.calendarName,
                        createUser.userId.as("create_id"), createUser.nickName.as("createNickName"),
                        modifyUser.userId.as("modifyId"), modifyUser.nickName.as("modifyNickName"),
                        content.startDateTime, content.endDateTime,
                        content.memo, content.isNotify, content.location, content.createDateTime, content.modifyDateTime))
                .from(content)
                .leftJoin(calendar).on(content.calendar.calendarId.eq(calendar.calendarId))
                .leftJoin(createUser).on(content.createUser.userId.eq(createUser.userId))
                .leftJoin(modifyUser).on(content.modifyUser.userId.eq(modifyUser.userId))
                .where(content.isDeleted.eq(isDeleted), calendar.calendarId.in(
                        JPAExpressions.select(
                                cur.calendar.calendarId
                        ).from(cur).where(cur.user.userId.eq(userId))
                ))
                .fetch();
    }

    public CalendarContentResponseDto selectCalendarContentByContentId(Long contentId, Boolean isDeleted){
        QCalendarContent content = QCalendarContent.calendarContent;
        QUser createUser = new QUser("createUser");
        QUser modifyUser = new QUser("modifyUser");

        return jpaQueryFactory
                .select(Projections.fields(CalendarContentResponseDto.class,
                        content.calendar.calendarId, content.calendar.calendarName,
                        content.contentId,
                        content.title,
                        createUser.userId.as("createUser"),
                        createUser.nickName.as("createNickName"),
                        modifyUser.userId.as("modifyUser"),
                        modifyUser.nickName.as("modifyNickName"),
                        content.startDateTime, content.endDateTime, content.memo,
                        content.isNotify, content.location, content.createDateTime, content.modifyDateTime))
                .from(content)
                .leftJoin(createUser).on(content.createUser.userId.eq(createUser.userId))
                .leftJoin(modifyUser).on(content.modifyUser.userId.eq(modifyUser.userId))
                .where(content.contentId.eq(contentId), content.isDeleted.eq(isDeleted))
                .fetchOne();
    }

    private BooleanExpression eqCalendar(Long calendarId){
        if(ObjectUtils.isEmpty(calendarId)){
            return null;
        }
        return calendar.calendarId.eq(calendarId);
    }
}
