package com.puzzly.api.repository.jpa.querydsl;

import org.springframework.data.repository.query.Param;

public interface CalendarLabelJpaRepositoryCustom {

    public Integer getMaxOrder(@Param("calendarId") Long calendarId);
}
