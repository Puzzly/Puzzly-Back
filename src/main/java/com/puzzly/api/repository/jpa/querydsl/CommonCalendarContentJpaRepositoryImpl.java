package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CommonCalendarContentResponseDto;
import com.puzzly.api.entity.QCommonCalendarContent;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CommonCalendarContentJpaRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public List<CommonCalendarContentResponseDto> selectCommonContentByDateTime(LocalDateTime startTargetDateTime, LocalDateTime limitTargetDateTime, Boolean isDeleted){
        QCommonCalendarContent ccc = new QCommonCalendarContent("ccc");
        return jpaQueryFactory
                .select(Projections.fields(CommonCalendarContentResponseDto.class,
                                ccc.contentId, ccc.endDateTime, ccc.startDateTime, ccc.isHoliday, ccc.title))
                .from(ccc)
                .where(
                        ccc.startDateTime.between(startTargetDateTime, limitTargetDateTime)
                ).fetch();
    }
}
