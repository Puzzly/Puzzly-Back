package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.entity.CalendarUserRelation;
import com.puzzly.api.entity.QCalendarUserRelation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;

@RequiredArgsConstructor
public class CalendarUserRelationJpaRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public boolean existsCalendarUserRelation(Long userId, Long calendarId, Boolean isDeleted){
        QCalendarUserRelation qcur = new QCalendarUserRelation("qcur");

        return jpaQueryFactory
                .selectFrom(qcur)
                .where(qcur.calendar.calendarId.eq(calendarId), qcur.user.userId.eq(userId), qcur.isDeleted.eq(isDeleted))
                .fetchOne() != null;
    }
}
