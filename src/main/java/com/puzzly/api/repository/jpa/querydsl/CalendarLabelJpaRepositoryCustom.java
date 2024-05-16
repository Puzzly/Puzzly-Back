package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarLabelResponseDto;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CalendarLabelJpaRepositoryCustom {

    public Integer getMaxOrder(@Param("calendarId") Long calendarId);
    public List<CalendarLabelResponseDto> selectCalendarLabelList(Long calendarId, int offset, int pageSize);

}
