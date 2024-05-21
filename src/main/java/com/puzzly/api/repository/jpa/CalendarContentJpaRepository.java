package com.puzzly.api.repository.jpa;

import com.puzzly.api.dto.response.CalendarContentAttachmentsResponseDto;
import com.puzzly.api.dto.response.CalendarContentRecurringInfoResponseDto;
import com.puzzly.api.dto.response.CalendarContentResponseDto;
import com.puzzly.api.entity.*;
import com.puzzly.api.repository.jpa.querydsl.CalendarContentJpaRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.puzzly.api.entity.QCalendar.calendar;

@Repository
public interface CalendarContentJpaRepository extends JpaRepository<CalendarContent, Long>, CalendarContentJpaRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CalendarContent SET isDeleted = true where calendar=:calendar")
    public void bulkUpdateIsDeletedCalendarContentByCalendar(Calendar calendar);

}
