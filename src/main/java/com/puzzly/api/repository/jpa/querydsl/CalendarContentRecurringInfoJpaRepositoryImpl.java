package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarContentRecurringInfoResponseDto;
import com.puzzly.api.entity.CalendarContentRecurringInfo;
import com.puzzly.api.entity.QCalendarContentRecurringInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalendarContentRecurringInfoJpaRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public CalendarContentRecurringInfoResponseDto selectCalendarContentRecurringInfo(Long contentId, Boolean isDeleted){
        QCalendarContentRecurringInfo qccri = new QCalendarContentRecurringInfo("qccri");

        return jpaQueryFactory
                .select(Projections.fields(CalendarContentRecurringInfoResponseDto.class,
                        qccri.infoId, qccri.calendarContent.contentId, qccri.recurringType,
                        qccri.period, qccri.recurringDate, qccri.recurringDay,
                        qccri.conditionCount, qccri.conditionEndDate, qccri.currentCount))
                .from(qccri)
                .where(qccri.calendarContent.contentId.eq(contentId), qccri.isDeleted.eq(isDeleted))
                .fetchOne();
    }
}
