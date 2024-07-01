package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarResponseDto;
import com.puzzly.api.entity.CalendarUserRelation;
import com.puzzly.api.entity.QCalendar;
import com.puzzly.api.entity.QCalendarUserRelation;
import com.puzzly.api.entity.QUser;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CalendarJpaRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public List<CalendarResponseDto> selectCalendarList(Long userId, int offset, int pageSize, boolean isDeleted){
        QCalendar calendar = QCalendar.calendar;
        /*QCalendar subCalendar = QCalendar.calendar;

         */
        QUser createUser = new QUser("createUser");
        QUser modifyUser = new QUser("modifyUser");
        QCalendarUserRelation cur = new QCalendarUserRelation("cur");
        /** QueryDSL은 스칼라 서브쿼리를 inner join으로 처리함. left null이면 resultSet이 null뜸.
         * 처음부터 left join으로 잡아주는게 옳다.
         * */
        return jpaQueryFactory
                .select(Projections.fields(CalendarResponseDto.class,
                        calendar.calendarId, calendar.calendarType, calendar.calendarName,
                        createUser.userId.as("createId"), modifyUser.userId,
//                        createUser.nickName.as("createNickName"),
                        modifyUser.userId.as("modifyId")
//                        modifyUser.nickName.as("modifyNickName")
                        ))
                .from(calendar)
                .leftJoin(createUser).on(calendar.createUser.userId.eq(createUser.userId))
                .leftJoin(modifyUser).on(calendar.modifyUser.userId.eq(modifyUser.userId))
                .where(calendar.calendarId.in(
                        JPAExpressions.select(
                                cur.calendar.calendarId
                        ).from(cur).where(cur.user.userId.eq(userId))
                ), calendar.isDeleted.eq(isDeleted))
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }
}
