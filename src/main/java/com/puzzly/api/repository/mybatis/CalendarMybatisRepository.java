package com.puzzly.api.repository.mybatis;

import com.puzzly.api.dto.response.CalendarResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@Deprecated(forRemoval = true)
public interface CalendarMybatisRepository {
    /* MIGRATED
    List<CalendarResponseDto> selectCalendarList(Long userId, int offset, int pageSize, boolean isDeleted);
     */
    /* NOUSE
    CalendarResponseDto selectCalendar(Long calendarId);
     */
}
