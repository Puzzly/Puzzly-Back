package com.puzzly.api.repository.jpa.querydsl;

import com.puzzly.api.dto.response.CalendarContentAttachmentsResponseDto;
import com.puzzly.api.dto.response.CalendarContentRecurringInfoResponseDto;
import com.puzzly.api.dto.response.CalendarContentResponseDto;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarContentJpaRepositoryCustom {
    public List<CalendarContentResponseDto> selectCalendarContentByDateTimeAndCalendar(Long userId, Long calendarId, LocalDateTime startDateTime, LocalDateTime limitStartDateTime, boolean isDeleted);

    public CalendarContentResponseDto selectCalendarContentByContentId(Long contentId, Boolean isDeleted);

}
