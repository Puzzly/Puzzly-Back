package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.entity.QCalendarLabel;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

@RequiredArgsConstructor
public class CalendarLabelJpaRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public Integer getMaxOrder(@Param("calendarId") Long calendarId) {
        QCalendarLabel calendarLabel = QCalendarLabel.calendarLabel;

        Integer result = jpaQueryFactory
                .select(calendarLabel.orderNum.max())
                .from(calendarLabel)
                .where(calendarLabel.calendar.calendarId.eq(calendarId))
                .fetchOne();

        return jpaQueryFactory
                .select(calendarLabel.orderNum.max() )
                .from(calendarLabel)
                .where(calendarLabel.calendar.calendarId.eq(calendarId))
                .fetchOne();
    }


}
