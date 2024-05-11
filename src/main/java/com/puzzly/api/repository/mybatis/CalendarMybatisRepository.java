package com.puzzly.api.repository.mybatis;

import com.puzzly.api.dto.response.CalendarResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CalendarMybatisRepository {
    List<CalendarResponseDto> getSimpleCalendarDtoListJoinRel(Long userId, int offset, int pageSize, boolean isDeleted);

    CalendarResponseDto getCalendar(Long calendarId);

}
