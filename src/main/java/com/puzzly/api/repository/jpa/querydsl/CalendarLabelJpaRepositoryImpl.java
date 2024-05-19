package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarLabelResponseDto;
import com.puzzly.api.entity.QCalendarLabel;
import com.puzzly.api.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import java.util.List;

@RequiredArgsConstructor
public class CalendarLabelJpaRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public Integer getMaxOrder(@Param("calendarId") Long calendarId) {
        QCalendarLabel calendarLabel = QCalendarLabel.calendarLabel;

        return jpaQueryFactory
                .select(calendarLabel.orderNum.max() )
                .from(calendarLabel)
                .where(calendarLabel.calendar.calendarId.eq(calendarId))
                .fetchOne();
    }

    public List<CalendarLabelResponseDto> selectCalendarLabelList(Long calendarId, int offset, int pageSize) {
        QCalendarLabel calendarLabel = QCalendarLabel.calendarLabel;
        QUser createUser = new QUser("createUser");
        QUser modifyUser = new QUser("modifyUser");

        return jpaQueryFactory
                .select(Projections.fields(CalendarLabelResponseDto.class,
                        calendarLabel.labelId,
                        calendarLabel.labelName,
                        calendarLabel.colorCode,
                        calendarLabel.orderNum,
                        createUser.userId.as("createId"),
                        createUser.nickName.as("createNickName"),
                        calendarLabel.createDateTime,
                        modifyUser.userId.as("modifyId"),
                        modifyUser.nickName.as("modifyNickName"),
                        calendarLabel.modifyDateTime
                ))
                .from(calendarLabel)
                    .leftJoin(createUser).on(calendarLabel.createUser.userId.eq(createUser.userId))
                    .leftJoin(modifyUser).on(calendarLabel.modifyUser.userId.eq(modifyUser.userId))
                .where(calendarLabel.calendar.calendarId.eq(calendarId),
                        calendarLabel.deleteUser.isNull())
                .orderBy(calendarLabel.orderNum.asc())
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }



}
